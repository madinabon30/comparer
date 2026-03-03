package uz.fb.comparer.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Getter
@Setter
@Table("ALL_TAB_COLUMNS")
public class ObjectEntity {
    private String columnName;
    private String dataType;
    private BigDecimal dataLength;
    private BigDecimal dataPrecision;
}
