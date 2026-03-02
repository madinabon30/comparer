package uz.fb.comparer.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.fb.comparer.model.ComparingModel;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ComparingService {

    private static final Logger log = LoggerFactory.getLogger(ComparingService.class);
    private final MetadataService metadataService;

    public ComparingService(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    public  void checkFilterRequest(String[] keys, String[] values) {
        for (int i = 0; i < keys.length; i++)
            if (validateMetadata(keys[i]) && validatePnflFormat(values[i]))  //validation
                compare(values[i]);


    }

    private  void compare(String pinfl) {
        List<String> pinflHashs = new ArrayList<>(List.of("8a9f0ea9d400aa60c214893766adff2deb388889a25089935fc061ee37533142"));

        if (pinflHashs.contains(getHashed(pinfl))){
            // writing to db
            log.info("Filtered pinfl in the list, the requested value is : {}", pinfl);
        }else {
            log.info("Not matched, the requested value is :{}", pinfl); // logging the process
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
        var ref = new Object() {
            boolean b;
        };
        String[] split = key.split("\\.");
        if (split.length == 2) {
            List<Map<String, Object>> viewColumns = metadataService.getViewColumns(split[0], split[1]);
            if (!viewColumns.isEmpty()) {
                  viewColumns.forEach(c -> {
                     ref.b = c.get("column_name").equals(split[1]) && (c.get("data_type").equals("numeric") ? (c.get("numeric_precision").equals(14)) : c.get("data_type").equals("varchar") && c.get("character_maximum_length").equals(14));
                 });
            }
        }
        //other cases should be dealt
        return ref.b;
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
