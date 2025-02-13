package dogveloper.vojoge.dog.service;

import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.dog.dto.DogDTO;
import dogveloper.vojoge.dog.repository.DogRepository;
import dogveloper.vojoge.social.user.User;
import dogveloper.vojoge.social.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class DogService {
    private final DogRepository dogRepository;
    private final UserService userService;

    public Dog saveDog(User user, DogDTO dogDTO) {
        return dogRepository.save(dogDTO.toEntity(user));
    }

    public List<Dog> findByUser(User user) {
        return dogRepository.findByUserId(user.getId());
    }

    // ✅ validation 메서드 이름 유지
    public boolean validation(Dog dog) {
        User user = userService.getAuthenticatedUser();
        return dog.getUser().equals(user);
    }

    public Dog updateDog(User user, Long id, DogDTO dogDTO) {
        Dog existingDog = findById(id);

        // ✅ 기존 validation() 활용
        if (!validation(existingDog)) {
            throw new IllegalArgumentException("Unauthorized to update this dog");
        }

        // ✅ DTO에서 엔티티 업데이트 수행
        dogDTO.updateEntity(existingDog);

        return dogRepository.save(existingDog);
    }

    public void deleteDog(User user, Long id) {
        Dog existingDog = findById(id);

        // ✅ 기존 validation() 활용
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
