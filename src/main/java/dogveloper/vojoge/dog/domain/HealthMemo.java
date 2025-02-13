package dogveloper.vojoge.dog.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "health_memo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HealthMemo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id", nullable = false)
    private Dog dog;

    @Column(nullable = false)
    private String title; // "심장사상충", "병원 방문", "기타"

    @Column(nullable = false)
    private LocalDate memoDate; // 기록 날짜

    private String notes; // 추가 메모

    @Builder
    public HealthMemo(Dog dog, String title, LocalDate memoDate, String notes) {
        this.dog = dog;
        this.title = title;
        this.memoDate = memoDate;
        this.notes = notes;
    }
}
