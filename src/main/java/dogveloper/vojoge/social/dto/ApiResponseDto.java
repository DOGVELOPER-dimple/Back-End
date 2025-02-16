package dogveloper.vojoge.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "API 응답 데이터")
public class ApiResponseDto {
    @Schema(description = "응답 메시지", example = "로그인 성공")
    private String message;

    @Schema(description = "추가 정보", example = "JWT 토큰 발급 완료")
    private String detail;
}
