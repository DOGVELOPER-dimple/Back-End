package dogveloper.vojoge.walk.dto.request;

import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.location.dto.RequestWalkLocationDto;
import dogveloper.vojoge.walk.domain.Walk;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestWalkDto {
    @NotBlank
    private LocalDateTime startTime;
    @NotBlank
    private LocalDateTime endTime;

    private Float dogConsumableCalories;

    private Float userConsumableCalories;

    private Float distance;

    private List<RequestWalkLocationDto> locationDtos;

    public Walk toEntity(Dog dog){
        return Walk.builder()
                .dog(dog)
                .requestWalkDto(this)
                .build();
    }
}
