package dogveloper.vojoge.social.controller;

import dogveloper.vojoge.jwt.JwtStorageService;
import dogveloper.vojoge.social.user.User;
import dogveloper.vojoge.social.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtStorageService jwtStorageService;
    @SneakyThrows
    @GetMapping("/login/google")
    @Operation(summary = "구글 로그인 //준상")
    public void googleLoginRedirect(HttpServletResponse response) {
        response.sendRedirect("http://localhost:8080/oauth2/authorization/google");
    }

    @SneakyThrows
    @GetMapping("/login/kakao")
    @Operation(summary = "카카오 로그인 //준상")
    public void kakaoLoginRedirect(HttpServletResponse response) {
        response.sendRedirect("http://localhost:8080/oauth2/authorization/kakao");
    }

    @GetMapping("/protected")
    @Operation(summary = "보호된 엔드포인트 (JWT 검증 테스트용) //준상")
    public ResponseEntity<Map<String, String>> protectedEndpoint() {
        return ResponseEntity.ok(Map.of("message", "이 요청은 인증되었습니다!"));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 처리 //준상", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Map<String, String>> logout() {
        User user = userService.getAuthenticatedUser();
        String token = jwtStorageService.getEmailByToken(user.getEmail());
        boolean isDeleted = jwtStorageService.deleteToken(token);
        String message = isDeleted ? "Redis에서 토큰 삭제 완료" : "Redis에서 토큰 삭제 실패";

        return ResponseEntity.ok(Map.of("message", "로그아웃 완료!", "status", message));
    }

    @GetMapping("/userinfo")
    @Operation(summary = "사용자 정보 조회 //준상", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Map<String, String>> getUserInfo() {
        User user = userService.getAuthenticatedUser();

        Map<String, String> userInfo = Map.of(
                "email", user.getEmail(),
                "name", user.getName(),
                "profileImage", user.getImage(),
                "provider", user.getProvider().name()
        );

        return ResponseEntity.ok(userInfo);
    }

    /*@GetMapping("/success")
    @Operation(summary = "이메일 기반 Redis에서 토큰 조회")
    public ResponseEntity<Map<String, String>> getTokenByEmail(@RequestParam("email") String email) {
        // 이메일 기반으로 Redis에서 토큰 조회
        String token = jwtStorageService.getTokenByEmail(email);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "해당 이메일로 저장된 토큰이 없습니다."));
        }

        return ResponseEntity.ok(Map.of(
                "token", token
        ));
    }*/
    @GetMapping("/success")
    public ResponseEntity<Map<String, String>> authSuccess(@RequestParam String token) {
        return ResponseEntity.ok(Map.of("message", "로그인 성공", "token", token));
    }

    @RestController
    public class RootController {
        @GetMapping("/")
        public ResponseEntity<String> rootHealthCheck() {
            return ResponseEntity.ok("Healthy");
        }
    }




}
