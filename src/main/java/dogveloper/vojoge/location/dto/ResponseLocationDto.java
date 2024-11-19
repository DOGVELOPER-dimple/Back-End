package dogveloper.vojoge.location.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseLocationDto {
    private float latitude;

    private float longitude;

    private LocalDateTime date;
}
