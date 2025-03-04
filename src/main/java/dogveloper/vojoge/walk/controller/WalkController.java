package dogveloper.vojoge.walk.controller;

import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.location.service.LocationService;
import dogveloper.vojoge.walk.domain.Walk;
import dogveloper.vojoge.walk.dto.request.RequestWalkDto;
import dogveloper.vojoge.walk.dto.response.ResponseAddWalkDto;
import dogveloper.vojoge.walk.dto.response.ResponseWalkDetailDto;
import dogveloper.vojoge.walk.dto.response.ResponseWalkDto;
import dogveloper.vojoge.walk.dto.response.ResponseWalkHistoryDto;
import dogveloper.vojoge.walk.service.WalkService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/walk")
@RequiredArgsConstructor
public class WalkController {
    private final WalkService walkService;
    private final LocationService locationService;

    @Operation(summary = "산책 저장", description = "산책 기록 저장")
    @PostMapping("/{dogId}/record")// 산책 기록 저장 기능
    public ResponseEntity<ResponseAddWalkDto> addWalkRepo(@PathVariable Long dogId, @RequestBody RequestWalkDto requestWalkDto){
        Walk walk = walkService.createWalk(dogId, requestWalkDto);
        boolean success = locationService.addLocation(walk, requestWalkDto.getLocationDtos());
        if(success){
            ResponseAddWalkDto responseAddWalkDto = new ResponseAddWalkDto("success", walk.getId());
            return ResponseEntity.ok(responseAddWalkDto);
        }else{
            ResponseAddWalkDto responseAddWalkDto = new ResponseAddWalkDto("fail", null);
            return ResponseEntity.badRequest().body(responseAddWalkDto);
        }
    }

    @Operation(summary = "산책 리스트", description = "강아지별 산책 기록 리스트 확인")
    @GetMapping("/{dogId}/history")// 산책 기록 리스트 확인
    public ResponseEntity<ResponseWalkDto<List<ResponseWalkHistoryDto>>> getWalkHistory(@PathVariable Long dogId){
        List<ResponseWalkHistoryDto> responseWalkDtoList = walkService.findAllByDogId(dogId);
        if(responseWalkDtoList!= null){
            ResponseWalkDto<List<ResponseWalkHistoryDto>> response = new ResponseWalkDto<>("success", responseWalkDtoList);
            return ResponseEntity.ok(response);
        }else{
            ResponseWalkDto<List<ResponseWalkHistoryDto>> response = new ResponseWalkDto<>("fail", null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "산책 경로 보기", description = "산책 기록 보기")
    @GetMapping("/{walkId}") // 산책 기록 경로 보기
    public ResponseEntity<ResponseWalkDto<ResponseWalkDetailDto>> getWalkDetail(@PathVariable Long walkId){
        ResponseWalkDetailDto responseWalkDetailDto = walkService.findDetailWalk(walkId);
        if(responseWalkDetailDto != null){
            ResponseWalkDto<ResponseWalkDetailDto> response = new ResponseWalkDto<>("success", responseWalkDetailDto);
            return ResponseEntity.ok(response);
        }else{
            ResponseWalkDto<ResponseWalkDetailDto> response = new ResponseWalkDto<>("fail", null);
            return ResponseEntity.ok(response);
        }
    }

    @Operation(summary = "산책 기록 삭제", description = "산책 기록 삭제")
    @DeleteMapping("/{walkId}")
    public ResponseEntity<Map<String, String>> deleteWalk(@PathVariable Long walkId){
        boolean b = walkService.deleteWalk(walkId);
        if(b){
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            return ResponseEntity.ok(response);
        }else{
            Map<String, String> response = new HashMap<>();
            response.put("status", "fail");
            return ResponseEntity.ok(response);
        }
    }
}
