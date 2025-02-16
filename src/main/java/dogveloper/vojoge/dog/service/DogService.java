package dogveloper.vojoge.dog.service;

import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.dog.dto.DogDTO;
import dogveloper.vojoge.dog.repository.DogRepository;
import dogveloper.vojoge.social.user.User;
import dogveloper.vojoge.social.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DogService {
    private final DogRepository dogRepository;
    private final UserService userService;

    @Transactional
    public Dog saveDog(User user, DogDTO dogDTO) {
        return dogRepository.save(dogDTO.toEntity(user));
    }

    public List<Dog> findByUser(User user) {
        return dogRepository.findByUserId(user.getId());
    }

    public boolean validation(Dog dog) {
        User user = userService.getAuthenticatedUser();
        return dog.getUser().equals(user);
    }
    @Transactional
    public Dog updateDog(User user, Long id, DogDTO dogDTO) {
        Dog existingDog = findById(id);

        if (!validation(existingDog)) {
            throw new IllegalArgumentException("Unauthorized to update this dog");
        }

        dogDTO.updateEntity(existingDog);
        return existingDog; // JPA 영속성 컨텍스트에 의해 자동 저장됨 (save 불필요)
    }

    public void deleteDog(User user, Long id) {
        Dog existingDog = findById(id);

        if (!validation(existingDog)) {
            throw new IllegalArgumentException("Unauthorized to delete this dog");
        }

        dogRepository.delete(existingDog);
    }

    public Dog findById(Long id) {
        return dogRepository.findById(id)
                .orElseThrow(()
                        -> new EntityNotFoundException("반려견 정보를 찾을 수 없습니다."));
    }
}
