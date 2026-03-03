package uz.fb.comparer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.coyote.Request;
import org.springframework.stereotype.Service;
import uz.fb.comparer.model.FilterDTO;
import uz.fb.comparer.model.RequestDTO;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * PinflFilterMatcher
 *
 * UZB PINFL struktura (14 xona):
 *  [1]     — jins + asr indeksi (1-6)
 *  [2-3]   — kun DD (01-31)
 *  [4-5]   — oy MM (01-12)
 *  [6-7]   — yil YY (00-99)
 *  [8-10]  — tuman/shahar kodi (001-999)
 *  [11-13] — tartib raqami (001-999)
 *  [14]    — nazorat raqami (0-9)
 *
 * Validatsiya ikki bosqichli:
 *  1. Regex — tez strukturaviy pre-filter
 *  2. LocalDate.of() — real kalendar tekshiruvi (Fevral 31 va hokazolar)
 */
@Service
public class PinflFilterMatcher {

    private static final Pattern PINFL_PATTERN = Pattern.compile(
        "^[1-6]"                                    // jins+asr indeksi: 1-6
        + "(0[1-9]|[12]\\d|3[01])"                  // DD: 01-31
        + "(0[1-9]|1[0-2])"                         // MM: 01-12
        + "\\d{2}"                                   // YY: 00-99
        + "(00[1-9]|0[1-9]\\d|[1-9]\\d{2})"         // tuman kodi: 001-999
        + "(00[1-9]|0[1-9]\\d|[1-9]\\d{2})"         // tartib raqami: 001-999
        + "\\d$"                                     // nazorat raqami: 0-9
    );

    private final Set<String> hashedPinflRegistry;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PinflFilterMatcher(Set<String> hashedPinflRegistry) {
        this.hashedPinflRegistry = Collections.unmodifiableSet(hashedPinflRegistry);
    }

    /**
     * Asosiy method.
     * JSON filter payload qabul qiladi, PINFL strukturasiga mos keluvchi
     * qiymatlarni hash qilib registry bilan taqqoslaydi.
     *
     * @return matched plain-text PINFLlar ro'yxati
     */
    public List<String> extractMatchedPinfl(String pinfl) {

        List<String> matched = new ArrayList<>();

                String trimmed = pinfl.trim();

                String hash = sha256Hex(trimmed);

                if (hashedPinflRegistry.contains(hash))
                    matched.add(trimmed);


        return matched;
    }

    // ---- PINFL Validatsiya ----

    /**
     * Ikki bosqichli PINFL validatsiya:
     * 1. Regex — strukturaviy pre-filter
     * 2. LocalDate.of() — real kalendar tekshiruvi
     */
    public static boolean isValidPinfl(String value) {
        if (value == null || value.length() != 14) return false;

        // Step 1: regex
        if (!PINFL_PATTERN.matcher(value).matches()) return false;

        // Step 2: real date validation
        try {
            int genderCenturyIndex = Character.getNumericValue(value.charAt(0));
            int day   = Integer.parseInt(value.substring(1, 3));
            int month = Integer.parseInt(value.substring(3, 5));
            int yy    = Integer.parseInt(value.substring(5, 7));

            int year = switch (genderCenturyIndex) {
                case 1, 2 -> 1800 + yy;
                case 3, 4 -> 1900 + yy;
                case 5, 6 -> 2000 + yy;
                default -> throw new DateTimeException("Invalid gender/century index: " + genderCenturyIndex);
            };

            LocalDate.of(year, month, day);
            return true;

        } catch (DateTimeException | NumberFormatException e) {
            return false;
        }
    }

    // ---- SHA-256 ----

    private static final ThreadLocal<MessageDigest> SHA256_DIGEST = ThreadLocal.withInitial(() -> {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    });

    public static String sha256Hex(String input) {
        MessageDigest digest = SHA256_DIGEST.get();
        digest.reset(); // muhim! avvalgi state ni tozalaydi
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder(64);
        for (byte b : hash) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

}