package dogveloper.vojoge.controller;

import dogveloper.vojoge.jwt.JwtTokenProvider;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class HomeController {

    private final JwtTokenProvider jwtTokenProvider;

    public HomeController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/loginSuccess")
    public String loginSuccess(@AuthenticationPrincipal OAuth2User oAuth2User, Model model) {
        // 사용자 이메일을 JWT에 저장
        String email = oAuth2User.getAttribute("email");
        String token = jwtTokenProvider.createToken(email); // JWT 생성

        model.addAttribute("name", oAuth2User.getAttribute("name"));
        model.addAttribute("token", token); // 뷰로 JWT 전달
        return "success";
    }
    @GetMapping("/protected")
    @ResponseBody
    public String protectedEndpoint() {
        return "이 요청은 인증되었습니다!";
    }
}
