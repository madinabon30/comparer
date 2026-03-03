package uz.fb.comparer.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class UserDetailsDTO {
    private String userCode;
    private String userName;
    private String filialCode;
    private String headerCode;
    private String localCode;
}
