package dogveloper.vojoge.dog.controller;

import dogveloper.vojoge.dog.dto.FoodIntakeDTO;
import dogveloper.vojoge.dog.service.FoodIntakeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dogs/{dogId}/food-intake")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class FoodIntakeController {
    private final FoodIntakeService foodIntakeService;

    @PostMapping
    @Operation(summary = "반려견 급여량 저장")
    public ResponseEntity<FoodIntakeDTO> saveFoodIntake(
            @PathVariable Long dogId,
            @RequestBody FoodIntakeDTO dto) {
        return ResponseEntity.ok(foodIntakeService.saveFoodIntake(dogId, dto));
    }

    @GetMapping
    @Operation(summary = "반려견 급여량 조회")
    public ResponseEntity<List<FoodIntakeDTO>> getFoodIntake(@PathVariable Long dogId) {
        return ResponseEntity.ok(foodIntakeService.getFoodIntakeByDog(dogId));
    }

    @PutMapping("/{intakeId}")
    @Operation(summary = "반려견 급여량 수정")
    public ResponseEntity<FoodIntakeDTO> updateFoodIntake(
            @PathVariable Long intakeId,
            @RequestBody FoodIntakeDTO dto) {
        return ResponseEntity.ok(foodIntakeService.updateFoodIntake(intakeId, dto));
    }

    @DeleteMapping("/{intakeId}")
    @Operation(summary = "반려견 급여량 삭제")
    public ResponseEntity<Void> deleteFoodIntake(@PathVariable Long intakeId) {
        foodIntakeService.deleteFoodIntake(intakeId);
        return ResponseEntity.noContent().build();
    }
}
