package dogveloper.vojoge.dog.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "dog_poop_log")
public class DogPoopLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id", nullable = false)
    private Dog dog;

    @Column(nullable = false)
    private LocalDateTime poopTime; // 배변 시간

    private String poopType; // 배변 유형 (예: "정상", "설사", "변비")
}
