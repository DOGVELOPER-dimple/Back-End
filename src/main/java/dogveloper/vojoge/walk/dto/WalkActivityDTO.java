package dogveloper.vojoge.walk.dto;

import dogveloper.vojoge.walk.domain.WalkActivity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WalkActivityDTO {
    private Long id;
    private LocalDateTime timestamp;
    private Double distance;
    private Integer bowelMovements;

    @Builder
    public WalkActivityDTO(Long id, LocalDateTime timestamp, Double distance, Integer bowelMovements) {
        this.id = id;
        this.timestamp = timestamp;
        this.distance = distance;
        this.bowelMovements = bowelMovements;
    }

    public static WalkActivityDTO fromEntity(WalkActivity activity) {
        return WalkActivityDTO.builder()
                .id(activity.getId())
                .timestamp(activity.getTimestamp())
                .distance(activity.getDistance())
                .bowelMovements(activity.getBowelMovements())
                .build();
    }
}
