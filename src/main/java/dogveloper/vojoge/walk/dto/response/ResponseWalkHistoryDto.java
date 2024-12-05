package dogveloper.vojoge.walk.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWalkHistoryDto {
    private Long walkId;
    private LocalDateTime date;
    private Float distance;
    private Float dogConsumableCalories;
    private Float userConsumableCalories;
}