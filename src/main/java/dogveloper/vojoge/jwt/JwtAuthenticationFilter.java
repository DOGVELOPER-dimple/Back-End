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
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        String path = request.getRequestURI();
        logger.info("[JwtAuthenticationFilter] 요청 URI: {}", path);

        // Swagger 및 OpenAPI 관련 요청 제외
        if (isSwaggerRequest(path)) {
            logger.info("[JwtAuthenticationFilter] Swagger 요청은 필터를 통과합니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // 로그인 요청 제외
        if (path.startsWith("/auth/login/")) {
            logger.info("[JwtAuthenticationFilter] /auth/login/** 요청은 필터를 통과합니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization 헤더 처리
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                String email = jwtTokenProvider.getEmailFromToken(token);
                if (email != null) {
                    setAuthentication(email, request);
                    logger.info("[JwtAuthenticationFilter] JWT 인증 성공: {}", email);
                } else {
                    logger.warn("[JwtAuthenticationFilter] JWT 토큰에서 이메일 추출 실패");
                }
            } else {
                logger.warn("[JwtAuthenticationFilter] JWT 토큰 유효하지 않음");
            }
        } else {
            logger.warn("[JwtAuthenticationFilter] Authorization 헤더 없음");
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Swagger 및 OpenAPI 관련 요청인지 확인하는 메서드
     */
    private boolean isSwaggerRequest(String path) {
        return path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars");
    }

    /**
     * SecurityContext에 인증 정보 설정
     */
    private void setAuthentication(String email, HttpServletRequest request) {
        logger.info("[JwtAuthenticationFilter] SecurityContext에 인증 정보 설정 - 이메일: {}", email);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                new User(email, "", Collections.emptyList()), null, Collections.emptyList()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
