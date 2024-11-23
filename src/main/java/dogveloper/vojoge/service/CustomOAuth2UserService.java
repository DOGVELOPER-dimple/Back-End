package dogveloper.vojoge.service;

import dogveloper.vojoge.entity.User;
import dogveloper.vojoge.repository.UserRepository;
import dogveloper.vojoge.entity.Provider;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Google 사용자 정보
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String sub = (String) attributes.get("sub");
        String name = (String) attributes.get("name");
        String email = (String) attributes.get("email");
        String picture = (String) attributes.get("picture");

        // User 엔티티 저장 또는 업데이트
        User user = userRepository.findBySub(sub)
                .orElseGet(() -> User.builder()
                        .sub(sub)
                        .name(name)
                        .email(email)
                        .image(picture)
                        .provider(Provider.GOOGLE)
                        .build());

        userRepository.save(user);

        return oAuth2User;
    }
}
