package dogveloper.vojoge.walk.dto.response;

import dogveloper.vojoge.location.dto.ResponseLocationDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWalkDetailDto {
    private Long walkId;

    private LocalDateTime startTime;


    private LocalDateTime endTime;


    private Float dogConsumableCalories;


    private Float userConsumableCalories;


    private Float distance;

    private List<ResponseLocationDto> responseLocationDtoList;

}