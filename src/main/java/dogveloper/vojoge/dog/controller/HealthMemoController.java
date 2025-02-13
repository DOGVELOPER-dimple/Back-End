package dogveloper.vojoge.dog.controller;

import dogveloper.vojoge.dog.dto.HealthMemoDTO;
import dogveloper.vojoge.dog.service.HealthMemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dogs/{dogId}/health-memo")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class HealthMemoController {
    private final HealthMemoService healthMemoService;

    @PostMapping
    @Operation(summary = "반려견 건강 관리 기록 추가")
    public ResponseEntity<HealthMemoDTO> saveHealthMemo(
            @PathVariable Long dogId,
            @RequestBody HealthMemoDTO dto) {
        return ResponseEntity.ok(healthMemoService.saveHealthMemo(dogId, dto));
    }

    @GetMapping
    @Operation(summary = "반려견 건강 관리 기록 조회")
    public ResponseEntity<List<HealthMemoDTO>> getHealthMemos(@PathVariable Long dogId) {
        return ResponseEntity.ok(healthMemoService.getHealthMemosByDog(dogId));
    }

    @DeleteMapping("/{memoId}")
    @Operation(summary = "반려견 건강 관리 기록 삭제")
    public ResponseEntity<Void> deleteHealthMemo(@PathVariable Long memoId) {
        healthMemoService.deleteHealthMemo(memoId);
        return ResponseEntity.noContent().build();
    }
}
