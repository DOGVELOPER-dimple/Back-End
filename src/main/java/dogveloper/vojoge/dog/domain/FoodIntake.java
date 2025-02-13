package dogveloper.vojoge.dog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "food_intake")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoodIntake {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id", nullable = false)
    private Dog dog;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDateTime intakeTime;

    @Builder
    public FoodIntake(Dog dog, Double amount, LocalDateTime intakeTime){
        this.dog = dog;
        this.amount = amount;
        this.intakeTime = intakeTime;
    }
}
