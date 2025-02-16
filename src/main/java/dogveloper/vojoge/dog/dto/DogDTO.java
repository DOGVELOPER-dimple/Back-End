package dogveloper.vojoge.dog.dto;

import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.social.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;

@Data
public class DogDTO {
    @Schema(hidden = true)
    private Long id;
    private String name;
    private int age;
    private double weight;
    private String gender;
    private String puppySpecies;
    private double height;
    private double legLength;
    private String bloodType;
    private String registrationNumber;
    private String image;
    private boolean isNeutered;
    private LocalDate recentCheckupDate;
    private LocalDate heartwormVaccinationDate;
    private LocalDate menstruationStartDate;
    private Integer menstruationDuration;
    private Integer menstruationCycle;

    @Builder
    public DogDTO(Long id, String name, int age, double weight, String gender, String puppySpecies,
                  double height, double legLength, String bloodType, String registrationNumber, String image,
                  LocalDate recentCheckupDate, LocalDate heartwormVaccinationDate,
                  LocalDate menstruationStartDate, Integer menstruationDuration, Integer menstruationCycle, boolean isNeutered) {
        this.id = id;
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
        this.isNeutered = isNeutered;
    }

    public Dog toEntity(User user) {
        return Dog.builder()
                .user(user)
                .name(this.name)
                .age(this.age)
                .weight(this.weight)
                .gender(this.gender)
                .puppySpecies(this.puppySpecies)
                .height(this.height)
                .legLength(this.legLength)
                .bloodType(this.bloodType)
                .registrationNumber(this.registrationNumber)
                .image(this.image == null || this.image.isEmpty() ? "/images/basephoto.png" : this.image)
                .recentCheckupDate(this.recentCheckupDate)
                .heartwormVaccinationDate(this.heartwormVaccinationDate)
                .menstruationStartDate(this.menstruationStartDate)
                .menstruationDuration(this.menstruationDuration)
                .menstruationCycle(this.menstruationCycle)
                .isNeutered(this.isNeutered)
                .build();
    }

    public static DogDTO fromEntity(Dog dog) {
        return DogDTO.builder()
                .id(dog.getId())
                .name(dog.getName())
                .age(dog.getAge())
                .weight(dog.getWeight())
                .gender(dog.getGender())
                .puppySpecies(dog.getPuppySpecies())
                .height(dog.getHeight())
                .legLength(dog.getLegLength())
                .bloodType(dog.getBloodType())
                .registrationNumber(dog.getRegistrationNumber())
                .image(dog.getImage())
                .recentCheckupDate(dog.getRecentCheckupDate())
                .heartwormVaccinationDate(dog.getHeartwormVaccinationDate())
                .menstruationStartDate(dog.getMenstruationStartDate())
                .menstruationDuration(dog.getMenstruationDuration())
                .menstruationCycle(dog.getMenstruationCycle())
                .isNeutered(dog.isNeutered())
                .build();
    }

    public void updateEntity(Dog dog) {
        if (Objects.nonNull(this.name)) dog.setName(this.name);
        if (this.age > 0) dog.setAge(this.age);
        if (this.weight > 0) dog.setWeight(this.weight);
        if (Objects.nonNull(this.gender)) dog.setGender(this.gender);
        if (Objects.nonNull(this.puppySpecies)) dog.setPuppySpecies(this.puppySpecies);
        if (this.height > 0) dog.setHeight(this.height);
        if (this.legLength > 0) dog.setLegLength(this.legLength);
        if (Objects.nonNull(this.bloodType)) dog.setBloodType(this.bloodType);
        if (Objects.nonNull(this.registrationNumber)) dog.setRegistrationNumber(this.registrationNumber);
        if (Objects.nonNull(this.image)) dog.setImage(this.image);
        if (Objects.nonNull(this.recentCheckupDate)) dog.setRecentCheckupDate(this.recentCheckupDate);
        if (Objects.nonNull(this.heartwormVaccinationDate)) dog.setHeartwormVaccinationDate(this.heartwormVaccinationDate);
        if (Objects.nonNull(this.menstruationStartDate)) dog.setMenstruationStartDate(this.menstruationStartDate);
        if (Objects.nonNull(this.menstruationDuration)) dog.setMenstruationDuration(this.menstruationDuration);
        if (Objects.nonNull(this.menstruationCycle)) dog.setMenstruationCycle(this.menstruationCycle);
        if (this.isNeutered != dog.isNeutered()) dog.setNeutered(this.isNeutered);
    }
}
