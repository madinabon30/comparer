package uz.fb.comparer.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.fb.comparer.model.ComparingModel;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;


@RequiredArgsConstructor
public class ComparingService {
    private static final Logger log = LoggerFactory.getLogger(ComparingService.class);
    private final MetadataService metadataService;

    public  void checkFilterRequest(String[] keys, String[] values) {
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }

        //mapping to ComparingModel
        ComparingModel req=new ComparingModel();
        Arrays.stream(ComparingModel.class.getFields()).forEach(a -> {
            try {
                a.setAccessible(true);
                req.getClass().getField(a.getName()).set(req, map.get(a.getName()));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        });

        req.setPinfl(values[0]);

        compare(req);

    }

    private  void compare(ComparingModel model) {
        List<String> pinflHashs = new ArrayList<>(List.of("8a9f0ea9d400aa60c214893766adff2deb388889a25089935fc061ee37533142"));

        String pinfl = model.getPinfl();

        if (pinflHashs.contains(getHashed(pinfl))){
            // writing to db
            log.info("Filtered pinfl in the list, the request is : {}", model.toString());
        }else {
            log.info("Not matched, the request is :{}", model.toString()); // logging the process
        }

    }


    private  String getHashed(String pinfl){
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(pinfl.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validateMetadata(String key) {
        //if it's like view.column_name

        String[] split = key.split("\\.");
        if (split.length == 2) {
            List<Map<String, Object>> viewColumns = metadataService.getViewColumns(split[0], split[1]);
            if (!viewColumns.isEmpty()) {
                 return viewColumns.forEach(c -> {
                     boolean b = c.get("column_name").equals(split[1]) ? c.get("data_type").equals("numeric") ? (c.get("numeric_precision").equals(14)) : c.get("data_type").equals("varchar") && c.get("character_maximum_length").equals(14);
                 });
            }
        }
        //other cases should be dealt
        return false;
    }

    private  boolean validatePnflFormat(String pinfl){
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy");
        //is pinfl length  14 digits
        if (pinfl.length() != 14 || !pinfl.matches("\\d+") ){
            log.info("Not matched with PINFL format requested field : {}", pinfl);
            return false;
        }
        Date parsedDate;
        String date = String.format("%s.%s.%s", pinfl.substring(1, 3), pinfl.substring(3, 5), pinfl.substring(5, 7));
        try{
             parsedDate = formatter.parse(date);
        } catch (ParseException e) {
            log.info("Not matched with PINFL format requested field : {}", pinfl);
            return false;
        }
        //starts with 3 (for men born in 19**), 4 (for women born in 19**), 5 (for men born in 2000+) and 6 (for women born in 2000+)
        if (!pinfl.startsWith("3") && !pinfl.startsWith("4") && !pinfl.startsWith("5") && !pinfl.startsWith("6")) {
            int compared = parsedDate.compareTo(new Date());
            return compared == -1;
        }else {
            log.info("Not matched with PINFL format requested field : {}", pinfl);
            return false;
        }
    }
}
