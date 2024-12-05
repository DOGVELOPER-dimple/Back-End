package dogveloper.vojoge.dog.domain;

import dogveloper.vojoge.social.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "dog")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
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

    @Builder
    public Dog(User user, String name, int age, double weight, String gender, String puppySpecies,
               double height, double legLength, String bloodType, String registrationNumber, String image) {
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
    }
}
