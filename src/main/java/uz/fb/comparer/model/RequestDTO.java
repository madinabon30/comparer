package uz.fb.comparer.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RequestDTO {
    private UserDetailsDTO user;
    private String sessionName;
    private String requestPage;
    private String viewName;
    private List<FilterDTO> filters;

}
