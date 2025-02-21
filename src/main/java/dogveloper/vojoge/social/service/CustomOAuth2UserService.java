package dogveloper.vojoge.social.service;

import dogveloper.vojoge.social.user.User;
import dogveloper.vojoge.social.user.Provider;
import dogveloper.vojoge.social.user.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();


        if ("kakao".equals(registrationId)) {
            return processKakaoUser(oAuth2User.getAttributes());
        } else if ("google".equals(registrationId)) {
            return processGoogleUser(oAuth2User.getAttributes());
        } else {
            throw new IllegalArgumentException("지원되지 않는 OAuth2 공급자: " + registrationId);
        }
    }

    private OAuth2User processKakaoUser(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        if (kakaoAccount == null) {
            throw new IllegalStateException("카카오 계정 정보를 가져오지 못했습니다.");
        }

        String sub = String.valueOf(attributes.get("id"));
        String email = (String) kakaoAccount.get("email");
        String nickname = (String) ((Map<String, Object>) kakaoAccount.get("profile")).get("nickname");
        String profileImage = (String) ((Map<String, Object>) kakaoAccount.get("profile")).get("profile_image_url");

        if (email == null) {
            throw new IllegalStateException("이메일 정보가 없습니다. 동의 항목을 확인해주세요.");
        }


        User user = userRepository.findByEmail(email).orElseGet(() -> User.builder()
                .sub(sub)
                .email(email)
                .name(nickname != null ? nickname : "카카오 사용자")
                .provider(Provider.KAKAO)
                .build());

        user.setImage(profileImage);
        userRepository.save(user);

        return new DefaultOAuth2User(
                Collections.singletonList(() -> "ROLE_USER"),
                attributes,
                "id"
        );
    }

    private OAuth2User processGoogleUser(Map<String, Object> attributes) {
        String sub = (String) attributes.get("sub");
        String name = (String) attributes.get("name");
        String email = (String) attributes.get("email");
        String picture = (String) attributes.get("picture");

        User user = userRepository.findByEmail(email).orElseGet(() -> User.builder()
                .sub(sub)
                .name(name)
                .email(email)
                .image(picture)
                .provider(Provider.GOOGLE)
                .build());

        userRepository.save(user);

        return new DefaultOAuth2User(
                Collections.singletonList(() -> "ROLE_USER"),
                attributes,
                "sub"
        );
    }
}