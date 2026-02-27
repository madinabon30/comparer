package uz.fb.comparer.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.fb.comparer.model.ComparingModel;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@RequiredArgsConstructor
public class ComparingService {
    private static final Logger log = LoggerFactory.getLogger(ComparingService.class);

    public static void checkFilterRequest(String[] keys, String[] values) {
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

    public static void checkFilterRequest(String pinfl) {


        //mapping to ComparingModel
        ComparingModel req=new ComparingModel();

        req.setPinfl(pinfl);

        compare(req);

    }

    private static void compare(ComparingModel model) {
        List<String> pinflHashs = new ArrayList<>(List.of("8a9f0ea9d400aa60c214893766adff2deb388889a25089935fc061ee37533142"));

        String pinfl = model.getPinfl();

        if (pinflHashs.contains(getHashed(pinfl))){
            // writing to db
            log.info("Filtered pinfl in the list, the request is : {}", model.toString());
        }else {
            log.info("Not matched, the request is :{}", model.toString()); // logging the process
        }

    }


    private static String getHashed(String pinfl){
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(pinfl.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
