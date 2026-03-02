package uz.fb.comparer.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ComparingService Tests")
@ExtendWith(MockitoExtension.class)
class ComparingServiceTest {

    @Mock
    private  MetadataService metadataService;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private  ComparingService comparingService;



    // ── The known PINFL that is in the hardcoded hash list ──
    // Hash: 8a9f0ea9d400aa60c214893766adff2deb388889a25089935fc061ee37533142
    private static final String MATCHED_PINFL   = "43112995540032";
    private static final String UNMATCHED_PINFL = "99999999999999";


        private Method getHashedMethod;

        @BeforeEach
        void setUp() throws NoSuchMethodException {
            metadataService= new MetadataService(jdbcTemplate);
            comparingService = new ComparingService(metadataService);
            getHashedMethod = ComparingService.class.getDeclaredMethod("getHashed", String.class);
            getHashedMethod.setAccessible(true);
        }

    // ─────────────────────────────────────────────────────────
    //  checkFilterRequest(String[] keys, String[] values)
    // ─────────────────────────────────────────────────────────


        @Test
        @DisplayName("Should NOT throw when keys and values are valid")
        void shouldNotThrowForValidArrays() {
            String[] keys   = {"users_v.code"};
            String[] values = {MATCHED_PINFL};

            assertDoesNotThrow(() ->
                    comparingService.checkFilterRequest(keys, values)
            );
        }

        @Test
        @DisplayName("Should NOT throw for unmatched PINFL in arrays")
        void shouldNotThrowForUnmatchedPinflInArrays() {
            String[] keys   = {"users_v.code"};
            String[] values = {UNMATCHED_PINFL};

            assertDoesNotThrow(() ->
                    comparingService.checkFilterRequest(keys, values)
            );
        }

        @Test
        @DisplayName("Should handle multiple key-value pairs")
        void shouldHandleMultipleKeyValuePairs() {
            String[] keys   = {"users_v.code", "users_v.name", "users_v.phone"};
            String[] values = {MATCHED_PINFL, "Alice", "active"};

            assertDoesNotThrow(() ->
                    comparingService.checkFilterRequest(keys, values)
            );
        }
    }