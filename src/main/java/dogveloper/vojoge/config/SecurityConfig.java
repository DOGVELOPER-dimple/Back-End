package dogveloper.vojoge.config;

import dogveloper.vojoge.jwt.JwtAuthenticationFilter;
import dogveloper.vojoge.social.handler.OAuth2LoginSuccessHandler;
import dogveloper.vojoge.social.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
                          CustomOAuth2UserService customOAuth2UserService,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.customOAuth2UserService = customOAuth2UserService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    var config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of(
                            "http://localhost:3000",
                            "http://localhost:8080",
                            "http://10.0.2.2:8080",
                            "https://4dce-222-118-182-61.ngrok-free.app",
                            "https://d87d-222-118-182-61.ngrok-free.app"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/login/**",
                                "/auth/logout",
                                "/oauth2/**",
                                "/static/**",
                                "/index.html",
                                "/",
                                "/css/**",
                                "/js/**",
                                "/chatPage",
                                "/chatroom/**",
                                "/chat/**",
                                "/subscribe/**",
                                "/publish/**",
                                "/auth/success"
                        ).permitAll()
                        .requestMatchers("/auth/protected").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureUrl("/auth/login/failure")
                )
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'; " +
                                        "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net; " +
                                        "connect-src 'self' ws://localhost:8080; " +
                                        "style-src 'self' 'unsafe-inline'; " +
                                        "font-src 'self' https://fonts.gstatic.com; " +
                                        "img-src 'self';")));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
