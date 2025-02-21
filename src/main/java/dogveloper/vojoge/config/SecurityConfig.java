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

        http.cors(cors -> {
            cors.configurationSource(request -> {
                var config = new CorsConfiguration();
                config.setAllowedOrigins(List.of(
                        "http://localhost:3000",
                        "http://localhost:8080",
                        "http://10.0.2.2:8080",
                        "http://3.34.179.184:8080",
                        "http://3.34.179.184",
                        "https://vojoge.site"
                ));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);
                return config;
            });
        });

        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers(
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
                            "/auth/success",
                            "/dogs",
                            "/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/swagger-resources/**",
                            "/webjars/**",
                            "/swagger-ui"
                    ).permitAll()
                    .requestMatchers("/auth/protected", "/dogs").authenticated()
                    .anyRequest().authenticated();
        });

        http.formLogin(AbstractHttpConfigurer::disable);

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.oauth2Login(oauth -> {
            oauth.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService));
            oauth.successHandler(oAuth2LoginSuccessHandler);
            oauth.failureUrl("/auth/login/failure");
        });

        http.headers(headers -> headers
                .contentSecurityPolicy(csp -> csp
                        .policyDirectives("default-src 'self'; " +
                                "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net; " +
                                "connect-src 'self' wss://vojoge.site; " +
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
