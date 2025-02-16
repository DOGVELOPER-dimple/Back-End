package dogveloper.vojoge.social.controller;

import dogveloper.vojoge.jwt.JwtStorageService;
import dogveloper.vojoge.jwt.JwtTokenProvider;
import dogveloper.vojoge.social.dto.ApiResponseDto;
import dogveloper.vojoge.social.dto.KakaoLoginRequest;
import dogveloper.vojoge.social.dto.UserInfoResponse;
import dogveloper.vojoge.social.user.Provider;
import dogveloper.vojoge.social.user.User;
import dogveloper.vojoge.social.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "구글 로그인 리다이렉트", description = "구글 OAuth 로그인 페이지로 이동합니다.")
    public void googleLoginRedirect(HttpServletResponse response) {
        response.sendRedirect("https://vojoge.site/oauth2/authorization/google");
    }

    @PostMapping("/login/kakao")
    @Operation(summary = "카카오 앱 로그인", description = "카카오 Access Token을 이용해 로그인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Map<String, Object>> kakaoLogin(@RequestBody KakaoLoginRequest request) {
        String kakaoAccessToken = request.getToken();

        if (kakaoAccessToken == null || kakaoAccessToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "토큰이 없습니다."));
        }

        try {
            // 카카오 API 호출하여 사용자 정보 가져오기
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + kakaoAccessToken);
            headers.set("Content-Type", "application/x-www-form-urlencoded");

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    requestEntity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.FOUND) {
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

            User user = userService.findByEmail(email);
            if (user == null) {
                System.out.println("신규 사용자 발견! 저장 시도: " + email);

                String kakaoId = String.valueOf(responseBody.get("id"));
                String sub = "kakao_" + kakaoId;

                user = User.builder()
                        .sub(sub)
                        .email(email)
                        .name(nickname)
                        .provider(Provider.KAKAO)
                        .image(profileImage)
                        .build();
                userService.saveUser(user);
            }

            // JWT 발급
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


    @GetMapping("/userinfo")
    @Operation(summary = "사용자 정보 조회", description = "로그인된 사용자의 정보를 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserInfoResponse> getUserInfo() {
        User user = userService.getAuthenticatedUser();
        UserInfoResponse response = new UserInfoResponse(
                user.getEmail(),
                user.getName(),
                user.getImage(),
                user.getProvider().name()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자의 JWT 토큰을 만료시킵니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponseDto> logout() {
        User user = userService.getAuthenticatedUser();
        String token = jwtStorageService.getEmailByToken(user.getEmail());
        boolean isDeleted = jwtStorageService.deleteToken(token);
        String message = isDeleted ? "Redis에서 토큰 삭제 완료" : "Redis에서 토큰 삭제 실패";

        return ResponseEntity.ok(new ApiResponseDto("로그아웃 완료!", message));
    }

    @DeleteMapping("/withdraw")
    @Operation(summary = "회원 탈퇴", description = "사용자의 계정을 삭제합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponseDto> withdrawUser() {
        User user = userService.getAuthenticatedUser();
        userService.deleteUser(user);
        jwtStorageService.deleteToken(jwtStorageService.getEmailByToken(user.getEmail()));

        return ResponseEntity.ok(new ApiResponseDto("회원 탈퇴 완료!", null));
    }
}
