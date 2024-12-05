package dogveloper.vojoge.dog.service;

import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.dog.dto.DogDTO;
import dogveloper.vojoge.dog.repository.DogRepository;
import dogveloper.vojoge.social.user.User;
import dogveloper.vojoge.social.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DogService {
    private final DogRepository dogRepository;
    private final UserRepository userRepository;

    public Dog saveDog(Long userId, DogDTO dogDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Dog dog = dogDTO.toEntity(user);
        return dogRepository.save(dog);
    }

    public Dog findById(Long id) {
        return dogRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Dog not found"));
    }

    public List<Dog> findByUserId(Long userId) {
        return dogRepository.findByUserId(userId);
    }

    public Dog updateDog(Long id, DogDTO dogDTO) {
        Dog existingDog = findById(id);
        if (dogDTO.getName() != null) existingDog.setName(dogDTO.getName());
        if (dogDTO.getAge() > 0) existingDog.setAge(dogDTO.getAge());
        if (dogDTO.getWeight() > 0) existingDog.setWeight(dogDTO.getWeight());
        // Other fields
        return dogRepository.save(existingDog);
    }

    public void deleteDog(Long id) {
        dogRepository.deleteById(id);
    }
}
