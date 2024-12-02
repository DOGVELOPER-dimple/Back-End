package dogveloper.vojoge.handler;

import dogveloper.vojoge.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    public OAuth2LoginSuccessHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 이메일 추출 로직 (구글 및 카카오 지원)
        String email = extractEmail(oAuth2User);
        if (email == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "이메일을 가져올 수 없습니다.");
            return;
        }

        // JWT 토큰 생성 및 Redis에 저장
        String token = jwtTokenProvider.createToken(email);

        // 리다이렉트 URL 설정
        String redirectUrl = "http://10.0.2.2:8080/auth/success?token=" + token; // 플러터에서 처리할 URL
        response.sendRedirect(redirectUrl);
    }

    private String extractEmail(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            Object kakaoAccount = oAuth2User.getAttribute("kakao_account");
            if (kakaoAccount instanceof Map) {
                Map<String, Object> accountMap = (Map<String, Object>) kakaoAccount;
                email = (String) accountMap.get("email");
            }
        }
        return email;
    }
}
