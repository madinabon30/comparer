package uz.fb.comparer.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FilterDTO {
    private String columnName;
    private List<String> filterValues;
}
