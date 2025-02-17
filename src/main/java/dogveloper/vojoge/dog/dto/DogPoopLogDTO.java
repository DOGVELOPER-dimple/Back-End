package dogveloper.vojoge.dog.dto;

import dogveloper.vojoge.dog.domain.DogPoopLog;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DogPoopLogDTO {
    @Schema(description = "배변 기록 ID")
    private Long id;

    @Schema(description = "반려견 ID")
    private Long dogId;

    @Schema(description = "배변 시간")
    private LocalDateTime poopTime;

    @Schema(description = "배변 유형")
    private String poopType;

    @Builder
    public DogPoopLogDTO(Long id, Long dogId, LocalDateTime poopTime, String poopType) {
        this.id = id;
        this.dogId = dogId;
        this.poopTime = poopTime;
        this.poopType = poopType;
    }

    public static DogPoopLogDTO fromEntity(DogPoopLog log) {
        return DogPoopLogDTO.builder()
                .id(log.getId())
                .dogId(log.getDog().getId())
                .poopTime(log.getPoopTime())
                .poopType(log.getPoopType())
                .build();
    }
}
