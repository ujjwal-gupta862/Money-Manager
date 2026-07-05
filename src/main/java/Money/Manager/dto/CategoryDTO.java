package Money.Manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;
    private String name;
    private String icon;
    private Long profileId;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
