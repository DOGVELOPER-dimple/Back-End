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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DogService {
    private final DogRepository dogRepository;
    private final UserService userService;

    private static final String IMAGE_UPLOAD_DIR = "dogveloper/vojoge/uploads/";

    private static final String DEFAULT_PROFILE_IMAGE =  "/images/basephoto.png";

    @Transactional
    public Dog saveDog(User user, DogDTO dogDTO) {
        if (dogDTO.getImage() == null || dogDTO.getImage().isEmpty()) {
            dogDTO.setImage(DEFAULT_PROFILE_IMAGE);
        }
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

        if (!existingDog.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized to update this dog");
        }

        dogDTO.updateEntity(existingDog);

        if (existingDog.getImage() == null || existingDog.getImage().isEmpty()) {
            existingDog.setImage(DEFAULT_PROFILE_IMAGE);
        }

        return existingDog;
    }

    public void deleteDog(User user, Long id) {
        Dog existingDog = findById(id);

        if (!validation(existingDog)) {
            throw new IllegalArgumentException("Unauthorized to delete this dog");
        }

        dogRepository.delete(existingDog);
    }
    @Transactional
    public String uploadDogImage(Long dogId, MultipartFile file) throws IOException {
        Dog dog = findById(dogId);

        Files.createDirectories(Paths.get(IMAGE_UPLOAD_DIR));

        String fileName = "dog_" + dogId + ".png";
        Path filePath = Paths.get(IMAGE_UPLOAD_DIR + fileName);

        file.transferTo(filePath.toFile());

        String imageUrl = "/uploads/" + fileName;
        dog.setImage(imageUrl);

        return imageUrl;
    }

    @Transactional
    public Dog updateDogImage(User user, Long id, String imageUrl) {
        Dog existingDog = findById(id);

        if (!existingDog.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized to update this dog");
        }

        existingDog.setImage(imageUrl);
        return existingDog;
    }

    @Transactional
    public void deleteDogImage(Long dogId) {
        Dog dog = findById(dogId);

        if (dog.getImage() != null && !dog.getImage().equals(DEFAULT_PROFILE_IMAGE)) {
            Path filePath = Paths.get(IMAGE_UPLOAD_DIR + dog.getImage().replace("/uploads/", ""));
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new RuntimeException("이미지 삭제 중 오류 발생", e);
            }
        }
        dog.setImage(DEFAULT_PROFILE_IMAGE);
    }

    public Dog findById(Long id) {
        return dogRepository.findById(id)
                .orElseThrow(()
                        -> new EntityNotFoundException("반려견 정보를 찾을 수 없습니다."));
    }
}
