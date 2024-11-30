package dogveloper.vojoge.alarm.domain;

import dogveloper.vojoge.alarm.enums.AlarmType;
import dogveloper.vojoge.dog.domain.Dog;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE alarm SET deleted_at = CURRENT_TIMESTAMP WHERE alarm_id = ?")
public class Alarm extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    private Long fromDogId;

    private Long targetId;

    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id")
    private Dog dog;

    @Builder
    public Alarm(Long id, AlarmType alarmType, Long fromDogId, Long targetId, String text, Dog dog) {
        this.id = id;
        this.alarmType = alarmType;
        this.fromDogId = fromDogId;
        this.targetId = targetId;
        this.text = text;
        this.dog = dog;
    }
}
