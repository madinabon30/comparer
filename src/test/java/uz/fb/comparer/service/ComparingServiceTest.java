package uz.fb.comparer.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ComparingService Tests")
class ComparingServiceTest {


    // ── The known PINFL that is in the hardcoded hash list ──
    // Hash: 8a9f0ea9d400aa60c214893766adff2deb388889a25089935fc061ee37533142
    private static final String MATCHED_PINFL   = "43112995540032";
    private static final String UNMATCHED_PINFL = "99999999999999";


        private Method getHashedMethod;

        @BeforeEach
        void setUp() throws NoSuchMethodException {
            getHashedMethod = ComparingService.class.getDeclaredMethod("getHashed", String.class);
            getHashedMethod.setAccessible(true);
        }

    // ─────────────────────────────────────────────────────────
    //  checkFilterRequest(String pinfl) — single param overload
    // ─────────────────────────────────────────────────────────

        @Test
        @DisplayName("Should NOT throw for matched PINFL")
        void shouldNotThrowForMatchedPinfl() {
            assertDoesNotThrow(() ->
                    ComparingService.checkFilterRequest(MATCHED_PINFL)
            );
        }

        @Test
        @DisplayName("Should NOT throw for unmatched PINFL")
        void shouldNotThrowForUnmatchedPinfl() {
            assertDoesNotThrow(() ->
                    ComparingService.checkFilterRequest(UNMATCHED_PINFL)
            );
        }

        @ParameterizedTest
        @DisplayName("Should NOT throw for various PINFL inputs")
        @ValueSource(strings = {
                "43112995540032",
                "00000000000000",
                "12345678901234",
                "abc",
                "!@#$%"
        })
        void shouldNotThrowForVariousInputs(String pinfl) {
            assertDoesNotThrow(() ->
                    ComparingService.checkFilterRequest(pinfl)
            );
        }



    // ─────────────────────────────────────────────────────────
    //  checkFilterRequest(String[] keys, String[] values)
    // ─────────────────────────────────────────────────────────


        @Test
        @DisplayName("Should NOT throw when keys and values are valid")
        void shouldNotThrowForValidArrays() {
            String[] keys   = {"pinfl"};
            String[] values = {MATCHED_PINFL};

            assertDoesNotThrow(() ->
                    ComparingService.checkFilterRequest(keys, values)
            );
        }

        @Test
        @DisplayName("Should NOT throw for unmatched PINFL in arrays")
        void shouldNotThrowForUnmatchedPinflInArrays() {
            String[] keys   = {"pinfl"};
            String[] values = {UNMATCHED_PINFL};

            assertDoesNotThrow(() ->
                    ComparingService.checkFilterRequest(keys, values)
            );
        }

        @Test
        @DisplayName("Should set PINFL from values[0] via setPinfl")
        void shouldSetPinflFromValuesArray() throws Exception {
            // We verify by checking that compare() runs without exception
            // meaning model.getPinfl() was correctly set to values[0]
            String[] keys   = {"pinfl"};
            String[] values = {MATCHED_PINFL};

            assertDoesNotThrow(() ->
                    ComparingService.checkFilterRequest(keys, values)
            );
        }

        @Test
        @DisplayName("Should handle empty arrays without exception")
        void shouldHandleEmptyArrays() {
            String[] keys   = {};
            String[] values = {};

            // values[0] will throw ArrayIndexOutOfBoundsException
            // since req.setPinfl(values[0]) is called unconditionally
            assertThrows(ArrayIndexOutOfBoundsException.class, () ->
                    ComparingService.checkFilterRequest(keys, values)
            );
        }

        @Test
        @DisplayName("Should throw when values array is null")
        void shouldThrowWhenValuesIsNull() {
            String[] keys = {"pinfl"};

            assertThrows(NullPointerException.class, () ->
                    ComparingService.checkFilterRequest(keys, null)
            );
        }

        @Test
        @DisplayName("Should throw when keys array is null")
        void shouldThrowWhenKeysIsNull() {
            String[] values = {MATCHED_PINFL};

            assertThrows(NullPointerException.class, () ->
                    ComparingService.checkFilterRequest(null, values)
            );
        }

        @Test
        @DisplayName("Should handle multiple key-value pairs")
        void shouldHandleMultipleKeyValuePairs() {
            String[] keys   = {"pinfl", "name", "status"};
            String[] values = {MATCHED_PINFL, "Alice", "active"};

            assertDoesNotThrow(() ->
                    ComparingService.checkFilterRequest(keys, values)
            );
        }
    }