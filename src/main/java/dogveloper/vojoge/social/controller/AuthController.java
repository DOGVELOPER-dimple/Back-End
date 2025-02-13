package dogveloper.vojoge.social.controller;

import dogveloper.vojoge.jwt.JwtStorageService;
import dogveloper.vojoge.jwt.JwtTokenProvider;
import dogveloper.vojoge.social.user.Provider;
import dogveloper.vojoge.social.user.User;
import dogveloper.vojoge.social.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtStorageService jwtStorageService;
    private final RestTemplate restTemplate;

    @SneakyThrows
    @GetMapping("/login/google")
    @Operation(summary = "구글 로그인 //준상")
    public void googleLoginRedirect(HttpServletResponse response) {
        response.sendRedirect("https://vojoge.site/oauth2/authorization/google");
    }

    /*
    @SneakyThrows
    @GetMapping("/login/kakao")
    @Operation(summary = "카카오 웹 로그인 (기존 방식) //준상")
    public void kakaoLoginRedirect(HttpServletResponse response) {
        response.sendRedirect("https://vojoge.site/oauth2/authorization/kakao");
    }
    */

    @PostMapping("/login/kakao")
    @Operation(summary = "카카오 앱 로그인 //준상")
    public ResponseEntity<Map<String, Object>> kakaoLogin(@RequestBody Map<String, String> body) {
        String kakaoAccessToken = body.get("token");
        if (kakaoAccessToken == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "토큰이 없습니다."));
        }

        try {
            // ✅ 카카오 API 호출하여 사용자 정보 가져오기
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + kakaoAccessToken);
            headers.set("Content-Type", "application/x-www-form-urlencoded");

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    request,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.FOUND) { // 302 응답 방지
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "카카오 로그인 인증 실패 (302 리다이렉트)"));
            }

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !responseBody.containsKey("kakao_account")) {
                return ResponseEntity.badRequest().body(Map.of("message", "카카오 사용자 정보 없음"));
            }

            Map<String, Object> kakaoAccount = (Map<String, Object>) responseBody.get("kakao_account");
            if (!kakaoAccount.containsKey("email")) {
                return ResponseEntity.badRequest().body(Map.of("message", "이메일 정보가 없습니다."));
            }

            String email = (String) kakaoAccount.get("email");
            String nickname = (String) ((Map<String, Object>) kakaoAccount.get("profile")).get("nickname");
            String profileImage = (String) ((Map<String, Object>) kakaoAccount.get("profile")).get("profile_image_url");

            // ✅ 사용자 정보 저장 또는 로그인 처리
            User user = userService.findByEmail(email);
            if (user == null) {
                System.out.println("✅ 신규 사용자 발견! 저장 시도: " + email);

                String kakaoId = String.valueOf(responseBody.get("id")); // ✅ 카카오 사용자 ID 가져오기
                String sub = "kakao_" + kakaoId; // ✅ sub 필드 값 설정

                user = User.builder()
                        .sub(sub) // ✅ sub 값 추가
                        .email(email)
                        .name(nickname)
                        .provider(Provider.KAKAO)
                        .image(profileImage)
                        .build();
                userService.saveUser(user);
            }


            // ✅ JWT 발급
            String jwtToken = jwtTokenProvider.createToken(email);

            return ResponseEntity.ok(Map.of(
                    "message", "로그인 성공",
                    "token", jwtToken
            ));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("message", "카카오 로그인 API 호출 실패", "error", e.getResponseBodyAsString()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "서버 오류", "error", e.getMessage()));
        }
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

    @GetMapping("/success")
    @Operation(summary = "json응답으로 토큰 //준상", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Map<String, String>> authSuccess(@RequestParam String token) {
        return ResponseEntity.ok(Map.of("message", "로그인 성공", "token", token));
    }

    @DeleteMapping("/withdraw")
    @Operation(summary = "회원 탈퇴 //준상", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Map<String, String>> withdrawUser(){
        User user = userService.getAuthenticatedUser();
        userService.deleteUser(user);
        jwtStorageService.deleteToken(jwtStorageService.getEmailByToken(user.getEmail()));

        return ResponseEntity.ok(Map.of("message","회원 탈퇴 완료!"));
    }
}
