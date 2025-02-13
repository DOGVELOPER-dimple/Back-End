package dogveloper.vojoge.test;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/")
    @Operation(summary = "((서버테스트용))")
    public ResponseEntity<String> rootHealthCheck() {
            return ResponseEntity.ok("Healthy");
        }


    @GetMapping("/protected")
    @Operation(summary = "((JWT 검증 테스트용)) //준상")
    public ResponseEntity<Map<String, String>> protectedEndpoint() {
        return ResponseEntity.ok(Map.of("message", "이 요청은 인증되었습니다!"));
    }
}
