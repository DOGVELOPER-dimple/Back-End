package dogveloper.vojoge.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "카카오 로그인 요청 DTO")
public class KakaoLoginRequest {
    @Schema(description = "카카오 Access Token", example = "your-access-token")
    private String token;
}

