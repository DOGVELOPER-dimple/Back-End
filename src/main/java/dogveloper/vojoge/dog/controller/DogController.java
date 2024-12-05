package dogveloper.vojoge.dog.controller;

import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.dog.dto.DogDTO;
import dogveloper.vojoge.dog.service.DogService;
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
    public ResponseEntity<Dog> createDog(@RequestParam Long userId, @RequestBody DogDTO dogDTO) {
        Dog createdDog = dogService.saveDog(userId, dogDTO);
        return ResponseEntity.ok(createdDog);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dog> getDogById(@PathVariable Long id) {
        Dog dog = dogService.findById(id);
        return ResponseEntity.ok(dog);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Dog>> getDogsByUser(@PathVariable Long userId) {
        List<Dog> dogs = dogService.findByUserId(userId);
        return ResponseEntity.ok(dogs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dog> updateDog(@PathVariable Long id, @RequestBody DogDTO dogDTO) {
        Dog dog = dogService.updateDog(id, dogDTO);
        return ResponseEntity.ok(dog);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDog(@PathVariable Long id) {
        dogService.deleteDog(id);
        return ResponseEntity.noContent().build();
    }
}
