package dogveloper.vojoge.walk.controller;

import dogveloper.vojoge.walk.dto.WalkActivityDTO;
import dogveloper.vojoge.walk.service.WalkActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dogs/{dogId}/walk-activities")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class WalkActivityController {
    private final WalkActivityService walkActivityService;

    @PostMapping
    @Operation(summary = "산책 활동 기록 추가")
    public ResponseEntity<WalkActivityDTO> saveWalkActivity(
            @PathVariable Long dogId,
            @RequestBody WalkActivityDTO dto) {
        return ResponseEntity.ok(walkActivityService.saveWalkActivity(dogId, dto));
    }

    @GetMapping
    @Operation(summary = "산책 활동 기록 조회 (기간 선택 가능)")
    public ResponseEntity<List<WalkActivityDTO>> getWalkActivities(
            @PathVariable Long dogId,
            @RequestParam String period) {
        return ResponseEntity.ok(walkActivityService.getWalkActivities(dogId, period));
    }

    @DeleteMapping("/{activityId}")
    @Operation(summary = "산책 활동 기록 삭제")
    public ResponseEntity<Void> deleteWalkActivity(@PathVariable Long activityId) {
        walkActivityService.deleteWalkActivity(activityId);
        return ResponseEntity.noContent().build();
    }
}
