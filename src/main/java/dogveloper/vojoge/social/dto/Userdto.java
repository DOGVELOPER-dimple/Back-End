package dogveloper.vojoge.social.dto;

import dogveloper.vojoge.social.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
public class Userdto {
    @Schema(description = "사용자 ID")
    private Long id;

    @Schema(description = "사용자 이름")
    private String name;

    @Schema(description = "사용자 이메일")
    private String email;

    @Schema(description = "프로필 이미지")
    private String image;

    @Schema(description = "알림 허용 여부")
    private boolean allowNotifications;

    @Builder
    public Userdto(Long id, String name, String email, String image, boolean allowNotifications) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.image = image;
        this.allowNotifications = allowNotifications;
    }

    public static Userdto fromEntity(User user) {
        return Userdto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .image(user.getImage())
                .allowNotifications(user.isAllowNotifications()) // 추가된 필드 반영
                .build();
    }
}
