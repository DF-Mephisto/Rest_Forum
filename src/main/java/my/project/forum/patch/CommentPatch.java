package my.project.forum.patch;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class CommentPatch {

    @Size(min=1, max=1000, message = "Comment must be between 1 and 1000 in length")
    private String text;

}
