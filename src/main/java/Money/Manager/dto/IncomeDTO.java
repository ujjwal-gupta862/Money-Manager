package Money.Manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeDTO{
    private Long id;
    private String name;
    private String icon;
    private LocalDate date;
    private String categoryName;
    private Long categoryId;
    private BigInteger amount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
