package dogveloper.vojoge.social.service;

import dogveloper.vojoge.jwt.JwtTokenProvider;
import dogveloper.vojoge.social.dto.LoginResponseDto;
import dogveloper.vojoge.social.dto.Userdto;
import dogveloper.vojoge.social.user.Provider;
import dogveloper.vojoge.social.user.User;
import dogveloper.vojoge.social.user.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {
    private static final Logger logger = LoggerFactory.getLogger(KakaoAuthService.class);
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;

    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    public ResponseEntity<LoginResponseDto> kakaoLogin(String kakaoToken) {
        if (kakaoToken == null || kakaoToken.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Map<String, Object> kakaoUserInfo = getKakaoUserInfo(kakaoToken);
        if (kakaoUserInfo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = (String) kakaoUserInfo.get("email");
        if (email == null) {
            return ResponseEntity.badRequest().build();
        }

        String nickname = (String) kakaoUserInfo.get("nickname");
        String profileImage = (String) kakaoUserInfo.get("profileImage");

        User user = findOrCreateUser(email, nickname, profileImage, kakaoUserInfo.get("id").toString());

        String accessToken = jwtTokenProvider.createToken(email);
        String refreshToken = jwtTokenProvider.createRefreshToken(email);

        return ResponseEntity.ok(new LoginResponseDto(accessToken, refreshToken, Userdto.fromEntity(user)));
    }

    private Map<String, Object> getKakaoUserInfo(String kakaoToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + kakaoToken);
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(KAKAO_USER_INFO_URL, HttpMethod.GET, requestEntity, Map.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.warn("카카오 API 응답 실패: {}", response.getStatusCode());
                return null;
            }

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !responseBody.containsKey("kakao_account")) {
                return null;
            }

            Map<String, Object> kakaoAccount = (Map<String, Object>) responseBody.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            return Map.of(
                    "id", responseBody.get("id"),
                    "email", kakaoAccount.get("email"),
                    "nickname", profile.get("nickname"),
                    "profileImage", profile.get("profile_image_url")
            );
        } catch (Exception e) {
            logger.error("카카오 사용자 정보 요청 중 오류 발생: {}", e.getMessage());
            return null;
        }
    }

    private User findOrCreateUser(String email, String nickname, String profileImage, String kakaoId) {
        User user = userService.findByEmail(email);

        if (user == null) { // Optional을 사용하지 않고 직접 체크
            user = User.builder()
                    .sub("kakao_" + kakaoId)
                    .email(email)
                    .name(nickname != null ? nickname : "카카오 사용자")
                    .provider(Provider.KAKAO)
                    .image(profileImage)
                    .allowNotifications(true)
                    .build();
            userService.saveUser(user);
        }

        return user;
    }
}
