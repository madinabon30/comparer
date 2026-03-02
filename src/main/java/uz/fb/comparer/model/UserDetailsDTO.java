package uz.fb.comparer.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class UserDetailsDTO {
    private String userCode;
    private String username;
    private String filialCode;
    private String headerCode;
    private String localCode;
}
