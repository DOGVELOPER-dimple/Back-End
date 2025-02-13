package dogveloper.vojoge.social.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("인증되지 않은 사용자입니다.");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자를 찾을 수 없습니다: " + email));
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    public void saveUser(User user) {
        System.out.println("🔥 DB에 사용자 저장 중: " + user.getEmail());
        userRepository.save(user);
        System.out.println("✅ 사용자 저장 완료: " + user.getEmail());
    }
    public void deleteUser(User user) {
        System.out.println("🔥 회원 탈퇴 진행 중: " + user.getEmail());
        userRepository.delete(user);
        System.out.println("✅ 회원 탈퇴 완료: " + user.getEmail());
    }
}
