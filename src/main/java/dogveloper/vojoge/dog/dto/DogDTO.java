package dogveloper.vojoge.dog.dto;

import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.social.user.User;
import lombok.Builder;
import lombok.Data;

@Data
public class DogDTO {
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

    @Builder
    public DogDTO(String name, int age, double weight, String gender, String puppySpecies, double height,
                  double legLength, String bloodType, String registrationNumber, String image) {
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
                .image(this.image)
                .build();
    }
    public static DogDTO fromEntity(Dog dog) {
        return DogDTO.builder()
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
                .build();
    }

}
