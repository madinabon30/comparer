package uz.fb.comparer.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.fb.comparer.model.FilterDTO;
import uz.fb.comparer.model.RequestDTO;
import uz.fb.comparer.model.UserDetailsDTO;
import uz.fb.comparer.repository.MetadataRepository;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ComparingService Tests")
@ExtendWith(MockitoExtension.class)
class ComparingServiceTest {

    @Mock
    private  MetadataService metadataService;

    @Mock
    private MetadataRepository metadataRepository;

    @InjectMocks
    private  ComparingService comparingService;



    // ── The known PINFL that is in the hardcoded hash list ──
    // Hash: 8a9f0ea9d400aa60c214893766adff2deb388889a25089935fc061ee37533142
    private static final String MATCHED_PINFL   = "43112995540032";
    private static final String UNMATCHED_PINFL = "99999999999999";


        private Method getHashedMethod;

        @BeforeEach
        void setUp(){
            metadataService= new MetadataService(metadataRepository);
            comparingService = new ComparingService(metadataService);
        }

    // ─────────────────────────────────────────────────────────
    //  checkFilterRequest(String[] keys, String[] values)
    // ─────────────────────────────────────────────────────────


        @Test
        @DisplayName("Should NOT throw when keys and values are valid")
        void shouldNotThrowForValidArrays() {
            assertDoesNotThrow(() ->
                    comparingService.checkFilterRequest(getRequestSuccess())
            );
        }

        @Test
        @DisplayName("Should NOT throw for unmatched PINFL in arrays")
        void shouldNotThrowForUnmatchedPinflInArrays() {
            assertDoesNotThrow(() ->
                    comparingService.checkFilterRequest(getRequestFail())
            );
        }

//        @Test
//        @DisplayName("Should handle multiple key-value pairs")
//        void shouldHandleMultipleKeyValuePairs() {
//            String[] keys   = {"users_v.code", "users_v.name", "users_v.phone"};
//            String[] values = {MATCHED_PINFL, "Alice", "active"};
//
//            assertDoesNotThrow(() ->
//                    comparingService.checkFilterRequest(keys, values)
//            );
//        }

    private RequestDTO getRequestSuccess(){
        return RequestDTO.builder()
                .user(UserDetailsDTO.builder()
                        .userName("analytic")
                        .build())
                .sessionName("10.50.70.88")
                .viewName("CLIENT_PHYSICAL_CURRENT")
                .filters(List.of(FilterDTO.builder()
                            .columnName("CODE_FILIAL")
                            .filterValues(List.of("00433"))
                            .build(), FilterDTO.builder()
                                .columnName("PINFL")
                                .filterValues(List.of(MATCHED_PINFL))
                        .build()))
                .build();

    }
    private RequestDTO getRequestFail(){
        return RequestDTO.builder()
                .user(UserDetailsDTO.builder()
                        .userName("analytic")
                        .build())
                .sessionName("10.50.70.88")
                .viewName("CLIENT_PHYSICAL_CURRENT")
                .filters(List.of(FilterDTO.builder()
                            .columnName("CODE_FILIAL")
                            .filterValues(List.of("00433"))
                            .build(), FilterDTO.builder()
                                .columnName("PINFL")
                                .filterValues(List.of(UNMATCHED_PINFL))
                        .build()))
                .build();

    }
}