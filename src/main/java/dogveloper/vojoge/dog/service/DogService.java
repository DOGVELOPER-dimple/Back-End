package dogveloper.vojoge.dog.service;

import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.dog.dto.DogDTO;
import dogveloper.vojoge.dog.repository.DogRepository;
import dogveloper.vojoge.social.user.User;
import dogveloper.vojoge.social.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DogService {
    private final DogRepository dogRepository;
    private final UserService userService;

    public Dog saveDog(User user, DogDTO dogDTO) {
        Dog dog = dogDTO.toEntity(user);
        return dogRepository.save(dog);
    }

    public List<Dog> findByUser(User user) {
        return dogRepository.findByUserId(user.getId());
    }

    public boolean validation(Dog dog){
        User user = userService.getAuthenticatedUser();
        return dog.getUser().equals(user);
    }
    public Dog updateDog(User user, Long id, DogDTO dogDTO) {
        Dog existingDog = findById(id);

        if (!existingDog.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized to update this dog");
        }

        if (dogDTO.getName() != null) existingDog.setName(dogDTO.getName());
        if (dogDTO.getAge() > 0) existingDog.setAge(dogDTO.getAge());
        if (dogDTO.getWeight() > 0) existingDog.setWeight(dogDTO.getWeight());
        if (dogDTO.getGender() != null) existingDog.setGender(dogDTO.getGender());
        if (dogDTO.getPuppySpecies() != null) existingDog.setPuppySpecies(dogDTO.getPuppySpecies());
        if (dogDTO.getHeight() > 0) existingDog.setHeight(dogDTO.getHeight());
        if (dogDTO.getLegLength() > 0) existingDog.setLegLength(dogDTO.getLegLength());
        if (dogDTO.getBloodType() != null) existingDog.setBloodType(dogDTO.getBloodType());
        if (dogDTO.getRegistrationNumber() != null) existingDog.setRegistrationNumber(dogDTO.getRegistrationNumber());
        if (dogDTO.getImage() != null) existingDog.setImage(dogDTO.getImage());

        return dogRepository.save(existingDog);
    }

    public void deleteDog(User user, Long id) {
        Dog existingDog = findById(id);

        if (!existingDog.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized to delete this dog");
        }

        dogRepository.deleteById(id);
    }

    public Dog findById(Long id) {
        return dogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dog not found"));
    }
}
