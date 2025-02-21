package dogveloper.vojoge.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtStorageService jwtStorageService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, JwtStorageService jwtStorageService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtStorageService = jwtStorageService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        String path = request.getRequestURI();

        if (isSwaggerRequest(path) || path.startsWith("/auth/login/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            // ✅ 블랙리스트 확인
            if (jwtStorageService.isBlacklisted(token)) {
                logger.warn("[JwtAuthenticationFilter] 블랙리스트에 등록된 토큰 감지 - 접근 거부");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "블랙리스트 토큰");
                return;
            }

            if (jwtTokenProvider.validateToken(token)) {
                String email = jwtTokenProvider.getEmailFromToken(token);
                if (email != null) {
                    setAuthentication(email, request);
                }
            }
        }

        filterChain.doFilter(request, response);
    }


    private boolean isSwaggerRequest(String path) {
        return path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars");
    }

    private void setAuthentication(String email, HttpServletRequest request) {
        logger.info("[JwtAuthenticationFilter] SecurityContext에 인증 정보 설정 - 이메일: {}", email);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                new User(email, "", Collections.emptyList()), null, Collections.emptyList()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}