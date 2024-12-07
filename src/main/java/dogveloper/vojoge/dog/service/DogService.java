package dogveloper.vojoge.dog.service;

import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.dog.dto.DogDTO;
import dogveloper.vojoge.dog.repository.DogRepository;
import dogveloper.vojoge.jwt.JwtTokenProvider;
import dogveloper.vojoge.social.user.User;
import dogveloper.vojoge.social.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DogService {
    private final DogRepository dogRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public Dog saveDog(String token, DogDTO dogDTO) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid token");
        }

        String userEmail = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + userEmail));

        Dog dog = dogDTO.toEntity(user);
        return dogRepository.save(dog);
    }


    public List<Dog> findByUserToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid token");
        }

        String userEmail = jwtTokenProvider.getEmailFromToken(token);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + userEmail));

        return dogRepository.findByUserId(user.getId());
    }


    public Dog updateDog(String token, Long id, DogDTO dogDTO) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid token");
        }

        String userEmail = jwtTokenProvider.getEmailFromToken(token);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

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


    public void deleteDog(String token, Long id) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid token");
        }

        String userEmail = jwtTokenProvider.getEmailFromToken(token);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

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
