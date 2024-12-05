package dogveloper.vojoge.walk.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAddWalkDto {
    private String status;
    private Long walkId;
}