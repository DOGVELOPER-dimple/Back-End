package dogveloper.vojoge.social.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

@Data
@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sub; // OAuth2 Provider의 고유 사용자 ID

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email = "no-email@provider.com"; // 기본 이메일 값 설정

    @Enumerated(EnumType.STRING)
    private Provider provider; // 소셜 로그인 제공자 (GOOGLE, KAKAO 등)

    private String image; // 프로필 이미지 URL

    /**
     * Spring Security 권한 반환
     * 기본적으로 "ROLE_USER" 권한을 부여
     */
    public List<GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
