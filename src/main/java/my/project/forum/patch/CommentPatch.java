package my.project.forum.patch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentPatch {

    @Size(min=1, max=1000, message = "Comment must be between 1 and 1000 in length")
    private String text;

}
