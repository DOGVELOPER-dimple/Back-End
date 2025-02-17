package dogveloper.vojoge.dog.controller;

import dogveloper.vojoge.dog.dto.DogPoopLogDTO;
import dogveloper.vojoge.dog.service.DogPoopLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/dogs/{dogId}/poop")
@SecurityRequirement(name = "bearerAuth")
public class DogPoopLogController {

    private final DogPoopLogService dogPoopLogService;

    @PostMapping
    @Operation(summary = "배변 기록 추가", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<DogPoopLogDTO> addPoopLog(@PathVariable Long dogId, @RequestBody DogPoopLogDTO dto) {
        return ResponseEntity.ok(dogPoopLogService.addPoopLog(dogId, dto));
    }

    @GetMapping
    @Operation(summary = "배변 기록 조회", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<List<DogPoopLogDTO>> getPoopLogs(
            @PathVariable Long dogId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return ResponseEntity.ok(dogPoopLogService.getPoopLogs(dogId, startDate, endDate));
    }

    @DeleteMapping("/{logId}")
    @Operation(summary = "배변 기록 삭제", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<String> deletePoopLog(@PathVariable Long logId) {
        dogPoopLogService.deletePoopLog(logId);
        return ResponseEntity.ok("배변 기록이 삭제되었습니다.");
    }
}
