package dogveloper.vojoge.dog.domain;

import dogveloper.vojoge.social.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "dog")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private double weight;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String puppySpecies;

    private double height;
    private double legLength;
    private String bloodType;
    private String registrationNumber;
    private String image;

    // ✅ 추가 필드
    private LocalDate recentCheckupDate; // 최근 검진 일자
    private LocalDate heartwormVaccinationDate; // 심장사상충 접종일
    private LocalDate menstruationStartDate; // 생리 시작일
    private Integer menstruationDuration; // 생리 지속일
    private Integer menstruationCycle; // 생리 주기

    @Builder
    public Dog(User user, String name, int age, double weight, String gender, String puppySpecies,
               double height, double legLength, String bloodType, String registrationNumber, String image,
               LocalDate recentCheckupDate, LocalDate heartwormVaccinationDate,
               LocalDate menstruationStartDate, Integer menstruationDuration, Integer menstruationCycle) {
        this.user = user;
        this.name = name;
        this.age = age;
        this.weight = weight;
        this.gender = gender;
        this.puppySpecies = puppySpecies;
        this.height = height;
        this.legLength = legLength;
        this.bloodType = bloodType;
        this.registrationNumber = registrationNumber;
        this.image = image;
        this.recentCheckupDate = recentCheckupDate;
        this.heartwormVaccinationDate = heartwormVaccinationDate;
        this.menstruationStartDate = menstruationStartDate;
        this.menstruationDuration = menstruationDuration;
        this.menstruationCycle = menstruationCycle;
    }
}
