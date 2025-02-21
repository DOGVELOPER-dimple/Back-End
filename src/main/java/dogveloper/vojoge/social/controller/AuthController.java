package dogveloper.vojoge.social.controller;

import dogveloper.vojoge.jwt.JwtStorageService;
import dogveloper.vojoge.jwt.JwtTokenProvider;
import dogveloper.vojoge.social.dto.LoginResponseDto;
import dogveloper.vojoge.social.dto.Userdto;
import dogveloper.vojoge.social.service.KakaoAuthService;
import dogveloper.vojoge.social.user.User;
import dogveloper.vojoge.social.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtStorageService jwtStorageService;
    private final KakaoAuthService kakaoAuthService;
    @SneakyThrows
    @GetMapping("/login/google")
    @Operation(summary = "구글 로그인 리다이렉트", description = "구글 OAuth 로그인 페이지로 이동합니다.")
    public void googleLoginRedirect(HttpServletResponse response) {
        response.sendRedirect("https://vojoge.site/oauth2/authorization/google");
    }

    @PostMapping("/login/kakao")
    @Operation(summary = "카카오 앱 로그인", description = "카카오 Access Token을 이용해 로그인합니다.")
    public ResponseEntity<LoginResponseDto> kakaoLogin(@RequestParam String kakaoToken) {
        return kakaoAuthService.kakaoLogin(kakaoToken);
    }


    @GetMapping("/userinfo")
    @Operation(summary = "사용자 정보 조회", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Userdto> getUserInfo() {
        User user = userService.getAuthenticatedUser();
        return ResponseEntity.ok(Userdto.fromEntity(user));
    }

    @PostMapping("/refresh")
    @Operation(summary = "액세스 토큰 갱신", security = @SecurityRequirement(name = "bearerAuth"), description = "리프레시 토큰을 이용해 새로운 액세스 토큰을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "액세스 토큰 갱신 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"message\": \"리프레시 토큰이 필요합니다.\" }"))),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"message\": \"유효하지 않은 리프레시 토큰\" }"))),
            @ApiResponse(responseCode = "401", description = "리프레시 토큰이 일치하지 않음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"message\": \"리프레시 토큰이 일치하지 않습니다.\" }")))
    })
    public ResponseEntity<LoginResponseDto> refreshAccessToken(@RequestParam String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String storedRefreshToken = jwtStorageService.getRefreshToken(email);
        if (!refreshToken.equals(storedRefreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String newAccessToken = jwtTokenProvider.createToken(email);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(email);
        User user = userService.findByEmail(email);

        return ResponseEntity.ok(new LoginResponseDto(newAccessToken, newRefreshToken, Userdto.fromEntity(user)));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"message\": \"로그아웃 완료\", \"detail\": \"토큰 무효화 완료\" }"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        User user = userService.getAuthenticatedUser();

        // ✅ Refresh Token 삭제
        jwtStorageService.deleteRefreshToken(user.getEmail());

        // ✅ Access Token 블랙리스트 추가 (무효화)
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            String accessToken = token.substring(7);
            jwtStorageService.addToBlacklist(accessToken);
        }

        return ResponseEntity.ok(Map.of(
                "message", "로그아웃 완료",
                "detail", "토큰 무효화 완료"
        ));
    }


    @DeleteMapping("/withdraw")
    @Operation(summary = "회원 탈퇴", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"message\": \"회원 탈퇴 완료\" }"))),
    })
    public ResponseEntity<Map<String, String>> withdrawUser(HttpServletRequest request) {
        User user = userService.getAuthenticatedUser();

        // ✅ 사용자 삭제
        userService.deleteUser(user);

        // ✅ Refresh Token 삭제
        jwtStorageService.deleteRefreshToken(user.getEmail());

        // ✅ Access Token 블랙리스트 추가 (무효화)
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            String accessToken = token.substring(7);
            jwtStorageService.addToBlacklist(accessToken);
        }

        return ResponseEntity.ok(Map.of("message", "회원 탈퇴 완료"));
    }

    @GetMapping("/success")
    @Operation(summary = "로그인 성공 응답", description = "액세스 토큰과 리프레시 토큰을 반환합니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"message\": \"잘못된 요청입니다.\" }")))
    })
    public ResponseEntity<LoginResponseDto> authSuccess(@RequestParam String token, @RequestParam String refreshToken) {
        String email = jwtTokenProvider.getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        User user = userService.findByEmail(email);
        return ResponseEntity.ok(new LoginResponseDto(token, refreshToken, Userdto.fromEntity(user)));
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