package dogveloper.vojoge.dog.dto;

import dogveloper.vojoge.dog.domain.FoodIntake;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FoodIntakeDTO {
    private Long id;
    private Double amount;
    private LocalDateTime intakeTime;

    @Builder
    public FoodIntakeDTO(Long id, Double amount, LocalDateTime intakeTime) {
        this.id = id;
        this.amount = amount;
        this.intakeTime = intakeTime;
    }

    public static FoodIntakeDTO fromEntity(FoodIntake foodIntake) {
        return FoodIntakeDTO.builder()
                .id(foodIntake.getId())
                .amount(foodIntake.getAmount())
                .intakeTime(foodIntake.getIntakeTime())
                .build();
    }

    public FoodIntake toEntity(dogveloper.vojoge.dog.domain.Dog dog) {
        return FoodIntake.builder()
                .dog(dog)
                .amount(this.amount)
                .intakeTime(this.intakeTime)
                .build();
    }
}
