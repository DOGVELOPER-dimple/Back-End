package dogveloper.vojoge.dog.controller;

import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.dog.dto.DogDTO;
import dogveloper.vojoge.dog.service.DogService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/dogs")
public class DogController {
    private final DogService dogService;

    @PostMapping
    @Operation(summary = "반려견 추가 //준상")
    public ResponseEntity<Dog> createDog(@RequestHeader("Authorization") String authorizationHeader, @RequestBody DogDTO dogDTO) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid or missing Authorization header");
        }

        String token = authorizationHeader.replace("Bearer ", "").trim();
        Dog createdDog = dogService.saveDog(token, dogDTO);
        return ResponseEntity.ok(createdDog);
    }



    @GetMapping("/{id}")
    @Operation(summary = "반려견 단일 조회 //준상")
    public ResponseEntity<DogDTO> getDogById(@PathVariable Long id) {
        Dog dog = dogService.findById(id);
        DogDTO dogDTO = DogDTO.fromEntity(dog);
        return ResponseEntity.ok(dogDTO);
    }

    @GetMapping
    @Operation(summary = "사용자 반려견 목록 조회 //준상")
    public ResponseEntity<List<DogDTO>> getDogsByUser(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid or missing Authorization header");
        }

        String token = authorizationHeader.replace("Bearer ", "").trim();

        List<DogDTO> dogDTOs = dogService.findByUserToken(token)
                .stream()
                .map(DogDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(dogDTOs);
    }


    @PutMapping("/{id}")
    @Operation(summary = "반려견 정보 수정 //준상")
    public ResponseEntity<DogDTO> updateDog(@RequestHeader("Authorization") String authorizationHeader,
                                            @PathVariable Long id,
                                            @RequestBody DogDTO dogDTO) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid or missing Authorization header");
        }

        String token = authorizationHeader.replace("Bearer ", "").trim();

        Dog updatedDog = dogService.updateDog(token, id, dogDTO);
        DogDTO responseDTO = DogDTO.fromEntity(updatedDog);

        return ResponseEntity.ok(responseDTO);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "반려견 삭제 //준상")
    public ResponseEntity<Void> deleteDog(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long id) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid or missing Authorization header");
        }

        String token = authorizationHeader.replace("Bearer ", "").trim();

        dogService.deleteDog(token, id);
        return ResponseEntity.noContent().build();
    }


    private String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // "Bearer " 제거
        }
        throw new IllegalArgumentException("Invalid Authorization header");
    }

}
