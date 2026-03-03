package uz.fb.comparer.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class FilterDTO {
    private String columnName;
    private List<String> filterValues;
}
