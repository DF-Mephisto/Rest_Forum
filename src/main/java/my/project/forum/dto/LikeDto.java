package my.project.forum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {

    private Long id;

    @NotNull(message = "Comment can't be null")
    private CommentDto comment;

}
