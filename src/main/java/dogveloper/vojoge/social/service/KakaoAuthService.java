package dogveloper.vojoge.social.service;

import dogveloper.vojoge.jwt.JwtTokenProvider;
import dogveloper.vojoge.social.dto.LoginResponseDto;
import dogveloper.vojoge.social.dto.Userdto;
import dogveloper.vojoge.social.user.Provider;
import dogveloper.vojoge.social.user.User;
import dogveloper.vojoge.social.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;

    public LoginResponseDto kakaoLogin(String kakaoToken) {
        if (kakaoToken == null || kakaoToken.isEmpty()) {
            throw new IllegalArgumentException("카카오 토큰이 비어 있습니다.");
        }

        Map<String, Object> kakaoUserInfo = getKakaoUserInfo(kakaoToken);
        if (kakaoUserInfo == null || !kakaoUserInfo.containsKey("kakao_account")) {
            throw new RuntimeException("카카오 사용자 정보를 가져올 수 없습니다.");
        }

        Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoUserInfo.get("kakao_account");
        String email = (String) kakaoAccount.get("email");
        String nickname = (String) ((Map<String, Object>) kakaoAccount.get("profile")).get("nickname");
        String profileImage = (String) ((Map<String, Object>) kakaoAccount.get("profile")).get("profile_image_url");

        if (email == null) {
            throw new RuntimeException("카카오 계정에서 이메일을 가져올 수 없습니다.");
        }

        User user = userService.findByEmail(email);
        if (user == null) {
            user = User.builder()
                    .sub("kakao_" + kakaoUserInfo.get("id"))
                    .email(email)
                    .name(nickname)
                    .provider(Provider.KAKAO)
                    .image(profileImage)
                    .allowNotifications(true)
                    .build();
            userService.saveUser(user);
        }

        String accessToken = jwtTokenProvider.createAccessToken(email);
        String refreshToken = jwtTokenProvider.createRefreshToken(email);

        return new LoginResponseDto(accessToken, refreshToken, Userdto.fromEntity(user));
    }

    private Map<String, Object> getKakaoUserInfo(String kakaoToken) {
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

        return response.getBody();
    }
}
