package dogveloper.vojoge.location.dto;

import dogveloper.vojoge.location.domain.Location;
import dogveloper.vojoge.walk.domain.Walk;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestWalkLocationDto {
    private float latitude;

    private float longitude;

    private LocalDateTime date;

    public Location toEntity(Walk walk){
        return Location.builder()
                .walk(walk)
                .requestWalkLocationDto(this)
                .build();
    }
}