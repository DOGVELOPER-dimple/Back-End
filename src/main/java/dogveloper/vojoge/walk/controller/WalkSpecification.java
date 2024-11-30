/*
package dogveloper.vojoge.walk.controller;

import dogveloper.vojoge.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Tag(name = "산책 API", description = "산책 API 문서입니다.")
public interface WalkSpecification {
    @Operation(summary = "사용자 전체 조회", description = "페이징이 없는 사용자 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "⭕ SUCCESS"
,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {@ExampleObject(
                                    value = "{\"message\":\"SUCCESS\",\"result\":{\"content\":[{\"postId\":1,\"title\":\"title\",\"content\":\"content\",\"category\":\"category\",\"userId\":1,\"nickName\":\"nickName\",\"userName\":\"userName\",\"createdDate\":\"2023/04/27 21:14\",\"lastModifiedDate\":\"2023/04/27 21:14\",\"imageUrl\":\"imageUrl\",\"view\":0,\"totalNumOfComments\":0,\"totalNumOfLikes\":0},{\"postId\":2,\"title\":\"title\",\"content\":\"content\",\"category\":\"category\",\"userId\":1,\"nickName\":\"nickName\",\"userName\":\"userName\",\"createdDate\":\"2023/05/13 00:15\",\"lastModifiedDate\":\"2023/05/13 00:15\",\"imageUrl\":\"imageUrl\",\"view\":0,\"totalNumOfComments\":0,\"totalNumOfLikes\":0}],\"pageable\":{\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"pageNumber\":0,\"pageSize\":20,\"unpaged\":false,\"paged\":true},\"totalPages\":1,\"totalElements\":2,\"last\":true,\"size\":20,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"numberOfElements\":2,\"first\":true,\"empty\":false}}"
                                )},
                            schema = @Schema(implementation = Response.class)
                    )

            ),
    })
    @GetMapping
    List<User> findAll();

    @Operation(summary = "페이징이 있는 사용자 전체 조회", description = "💡 페이징이 있는 사용자 조회")
    @GetMapping("/findAllPage")
    List<User> findAll(@RequestParam(defaultValue="1") int page, @RequestParam(defaultValue="10") int size);
}
*/
