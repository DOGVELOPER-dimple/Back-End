package dogveloper.vojoge.social.handler;

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

        String email = extractEmail(oAuth2User);
        if (email == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "이메일을 가져올 수 없습니다.");
            return;
        }

        // ✅ Access Token & Refresh Token 생성
        String accessToken = jwtTokenProvider.createToken(email);
        String refreshToken = jwtTokenProvider.createRefreshToken(email);

        // ✅ Access Token & Refresh Token을 함께 전달하도록 수정
        String redirectUrl = String.format("https://vojoge.site/auth/success?token=%s&refreshToken=%s", accessToken, refreshToken);
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
