package dogveloper.vojoge.controller;

import dogveloper.vojoge.entity.User;
import dogveloper.vojoge.jwt.JwtStorageService;
import dogveloper.vojoge.jwt.JwtTokenProvider;
import dogveloper.vojoge.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtStorageService jwtStorageService;
    private final UserRepository userRepository;

    public AuthController(JwtTokenProvider jwtTokenProvider, JwtStorageService jwtStorageService, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtStorageService = jwtStorageService;
        this.userRepository = userRepository;
    }

    @SneakyThrows
    @GetMapping("/login/google")
    @Operation(summary = "구글 로그인 //준상")
    public void googleLoginRedirect(HttpServletResponse response){
        response.sendRedirect("http://localhost:8080/oauth2/authorization/google");
    }
    @SneakyThrows
    @GetMapping("/login/kakao")
    @Operation(summary = "카카오 로그인 //준상")
    public void kakaoLoginRedirect(HttpServletResponse response) {
        response.sendRedirect("http://localhost:8080/oauth2/authorization/kakao");
    }

/*
    @GetMapping("/login/urls")
    @Operation(summary = "카카오, 구글 로그인 url 제공 //준상")
    public ResponseEntity<Map<String, String>> getLoginUrls() {
        Map<String, String> loginUrls = new HashMap<>();
        loginUrls.put("google", "http://localhost:8080/oauth2/authorization/google");
        loginUrls.put("kakao", "http://localhost:8080/oauth2/authorization/kakao");
        return ResponseEntity.ok(loginUrls);
    }
*/
    @GetMapping("/protected")
    @Operation(summary = "보호된 엔드포인트 (JWT 검증 테스트용) //준상")
    public ResponseEntity<Map<String, String>> protectedEndpoint() {
        return ResponseEntity.ok(Map.of("message", "이 요청은 인증되었습니다!"));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 처리 //준상")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            System.out.println("로그아웃 처리된 토큰: " + token);

            // Redis에서 토큰 삭제
            boolean isDeleted = jwtStorageService.deleteToken(token);
            if (isDeleted) {
                System.out.println("Redis에서 토큰 삭제 완료");
            } else {
                System.err.println("Redis에서 토큰 삭제 실패");
            }
        }
        return ResponseEntity.ok(Map.of("message", "로그아웃 완료!"));
    }

    @GetMapping("/userinfo")
    @Operation(summary = "사용자 정보 조회 //준상")
    public ResponseEntity<Map<String, String>> getUserInfo(@RequestHeader("Authorization") String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or missing Authorization header"));
        }

        String token = authorization.substring(7); // "Bearer " 제거
        String email = jwtTokenProvider.getEmailFromToken(token); // JWT에서 이메일 추출

        // 이메일로 사용자 정보 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + email));

        // 사용자 정보를 JSON으로 반환
        Map<String, String> userInfo = Map.of(
                "email", user.getEmail(),
                "name", user.getName(),
                "profileImage", user.getImage(),
                "provider", user.getProvider().name() // Provider 정보 추가 (Google/Kakao)
        );

        return ResponseEntity.ok(userInfo);
    }


}
