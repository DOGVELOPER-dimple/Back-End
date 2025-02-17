package dogveloper.vojoge.dog.controller;

import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.dog.dto.DogDTO;
import dogveloper.vojoge.dog.service.DogService;
import dogveloper.vojoge.social.user.User;
import dogveloper.vojoge.social.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/dogs")
@SecurityRequirement(name = "bearerAuth")
public class DogController {
    private final DogService dogService;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "반려견 추가 //준상", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<Dog> createDog(@RequestBody DogDTO dogDTO) {
        User user = userService.getAuthenticatedUser();
        dogDTO.setId(null);
        Dog createdDog = dogService.saveDog(user, dogDTO);
        return ResponseEntity.ok(createdDog);
    }

    @GetMapping("/{id}")
    @Operation(summary = "반려견 단일 조회 //준상", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<DogDTO> getDogById(@PathVariable Long id) {
        Dog dog = dogService.findById(id);
        DogDTO dogDTO = DogDTO.fromEntity(dog);
        return ResponseEntity.ok(dogDTO);
    }

    @GetMapping
    @Operation(summary = "사용자 반려견 목록 조회 //준상", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<List<DogDTO>> getDogsByUser() {
        User user = userService.getAuthenticatedUser();
        List<DogDTO> dogDTOs = dogService.findByUser(user)
                .stream()
                .map(DogDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(dogDTOs);
    }

    @PutMapping("/{id}")
    @Operation(summary = "반려견 정보 수정 //준상", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<DogDTO> updateDog(@PathVariable Long id, @RequestBody DogDTO dogDTO) {
        User user = userService.getAuthenticatedUser();
        Dog updatedDog = dogService.updateDog(user, id, dogDTO);
        DogDTO responseDTO = DogDTO.fromEntity(updatedDog);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "반려견 삭제 //준상", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<Void> deleteDog(@PathVariable Long id) {
        User user = userService.getAuthenticatedUser();
        dogService.deleteDog(user, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/upload")
    @Operation(summary = "반려견 프로필 사진 업로드")
    public ResponseEntity<String> uploadDogImage(@PathVariable Long id, @RequestParam("image") MultipartFile file) throws IOException {
        String imageUrl = dogService.uploadDogImage(id, file);
        return ResponseEntity.ok(imageUrl);
    }
    @PutMapping("/{id}/image")
    @Operation(summary = "반려견 프로필 사진 변경")
    public ResponseEntity<DogDTO> updateDogImage(@PathVariable Long id, @RequestBody DogDTO dogDTO) {
        User user = userService.getAuthenticatedUser();
        Dog updatedDog = dogService.updateDogImage(user, id, dogDTO.getImage());
        return ResponseEntity.ok(DogDTO.fromEntity(updatedDog));
    }
    @DeleteMapping("/{id}/image")
    @Operation(summary = "반려견 프로필 사진 삭제")
    public ResponseEntity<String> deleteDogImage(@PathVariable Long id) {
        dogService.deleteDogImage(id);
        return ResponseEntity.ok("반려견 프로필 사진이 삭제되었습니다.");
    }
}
