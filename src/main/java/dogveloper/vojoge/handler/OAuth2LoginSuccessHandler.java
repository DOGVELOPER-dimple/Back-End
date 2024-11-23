package dogveloper.vojoge.handler;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            org.springframework.security.core.Authentication authentication
    ) throws java.io.IOException, jakarta.servlet.ServletException {
        // 추가 작업 로직
        super.onAuthenticationSuccess(request, response, authentication);
    }
}