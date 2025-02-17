package dogveloper.vojoge.social.controller;

import dogveloper.vojoge.jwt.JwtStorageService;
import dogveloper.vojoge.jwt.JwtTokenProvider;
import dogveloper.vojoge.social.dto.LoginResponseDto;
import dogveloper.vojoge.social.dto.Userdto;
import dogveloper.vojoge.social.user.Provider;
import dogveloper.vojoge.social.user.User;
import dogveloper.vojoge.social.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
    public ResponseEntity<LoginResponseDto> kakaoLogin(@RequestParam String kakaoToken) {
        if (kakaoToken == null || kakaoToken.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + kakaoToken);
            headers.set("Content-Type", "application/x-www-form-urlencoded");

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    requestEntity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.FOUND) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !responseBody.containsKey("kakao_account")) {
                return ResponseEntity.badRequest().build();
            }

            Map<String, Object> kakaoAccount = (Map<String, Object>) responseBody.get("kakao_account");
            if (!kakaoAccount.containsKey("email")) {
                return ResponseEntity.badRequest().build();
            }

            String email = (String) kakaoAccount.get("email");
            String nickname = (String) ((Map<String, Object>) kakaoAccount.get("profile")).get("nickname");
            String profileImage = (String) ((Map<String, Object>) kakaoAccount.get("profile")).get("profile_image_url");

            User user = userService.findByEmail(email);
            if (user == null) {
                user = User.builder()
                        .sub("kakao_" + responseBody.get("id"))
                        .email(email)
                        .name(nickname)
                        .provider(Provider.KAKAO)
                        .image(profileImage)
                        .allowNotifications(true)
                        .build();
                userService.saveUser(user);
            }

            // WT 토큰 발급
            String jwtToken = jwtTokenProvider.createToken(email);

            // 응답 DTO 생성 후 반환
            return ResponseEntity.ok(new LoginResponseDto(jwtToken, Userdto.fromEntity(user)));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }


    @GetMapping("/userinfo")
    @Operation(summary = "사용자 정보 조회", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Userdto> getUserInfo() {
        User user = userService.getAuthenticatedUser();
        return ResponseEntity.ok(Userdto.fromEntity(user));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"message\": \"로그아웃 완료\", \"detail\": \"Redis에서 토큰 삭제 완료\" }"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Map<String, String>> logout() {
        User user = userService.getAuthenticatedUser();
        String token = jwtStorageService.getEmailByToken(user.getEmail());
        boolean isDeleted = jwtStorageService.deleteToken(token);
        String message = isDeleted ? "Redis에서 토큰 삭제 완료" : "Redis에서 토큰 삭제 실패";

        return ResponseEntity.ok(Map.of("message", "로그아웃 완료", "detail", message));
    }

    @DeleteMapping("/withdraw")
    @Operation(summary = "회원 탈퇴", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"message\": \"회원 탈퇴 완료\" }"))),
    })
    public ResponseEntity<Map<String, String>> withdrawUser() {
        User user = userService.getAuthenticatedUser();
        userService.deleteUser(user);
        jwtStorageService.deleteToken(jwtStorageService.getEmailByToken(user.getEmail()));

        return ResponseEntity.ok(Map.of("message", "회원 탈퇴 완료"));
    }

    @PostMapping("/notification-settings")
    @Operation(summary = "사용자의 알림 설정 변경", security = @SecurityRequirement(name = "bearerAuth"), description = "사용자의 알림 허용 여부를 업데이트합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알림 설정 변경 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"message\": \"알림 설정 변경 성공\", \"detail\": \"사용자의 알림 허용 상태가 업데이트되었습니다.\" }")))
    })
    public ResponseEntity<Map<String, String>> updateNotificationSettings(@RequestParam boolean allowNotifications) {
        User user = userService.getAuthenticatedUser();
        userService.updateNotificationPreference(user.getId(), allowNotifications);

        return ResponseEntity.ok(Map.of(
                "message", "알림 설정 변경 성공",
                "detail", "사용자의 알림 허용 상태가 업데이트되었습니다."
        ));
    }
}
