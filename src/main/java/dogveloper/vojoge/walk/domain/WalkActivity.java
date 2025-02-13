package dogveloper.vojoge.walk.domain;

import dogveloper.vojoge.dog.domain.Dog;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "walk_activity")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalkActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id", nullable = false)
    private Dog dog;

    @Column(nullable = false)
    private LocalDateTime timestamp; // 기록 시간

    @Column(nullable = false)
    private Double distance; // 이동 거리 (m)

    @Column(nullable = false)
    private Integer bowelMovements; // 배변 횟수

    @Builder
    public WalkActivity(Dog dog, LocalDateTime timestamp, Double distance, Integer bowelMovements) {
        this.dog = dog;
        this.timestamp = timestamp;
        this.distance = distance;
        this.bowelMovements = bowelMovements;
    }
}
