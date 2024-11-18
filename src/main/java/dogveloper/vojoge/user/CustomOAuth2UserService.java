package dogveloper.vojoge.user;

import dogveloper.vojoge.jwt.JwtTokenProvider;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    public CustomOAuth2UserService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String sub = oAuth2User.getAttribute("sub");
        String name = oAuth2User.getAttribute("name");
        String email = oAuth2User.getAttribute("email");
        String image = oAuth2User.getAttribute("picture");

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            user.setName(name);
            user.setImage(image);
        } else {
            user = new User();
            user.setSub(sub);
            user.setName(name);
            user.setEmail(email);
            user.setProvider(Provider.valueOf(provider.toUpperCase()));
            user.setImage(image);
            userRepository.save(user);
        }

        // JWT 토큰 생성
        String jwtToken = jwtTokenProvider.generateToken(email);

        // JWT 토큰을 반환할 수 있도록 설정
        // 예를 들어, 이 값을 `OAuth2User` 객체의 속성으로 설정하거나 필요한 곳에서 사용

        return oAuth2User;  // 실제로는 JWT를 응답으로 반환하거나 세션에 저장하는 등의 추가 작업 필요
    }
}
