package my.project.forum.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.project.forum.entity.Comment;
import my.project.forum.entity.Topic;
import my.project.forum.entity.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto implements Serializable {

    private Long id;

    @NotBlank(message = "Message can't be empty")
    @Size(min=1, max=1000, message = "Comment must be between 1 and 1000 in length")
    private String text;

    @NotNull(message = "Parent topic can't be null")
    private TopicDto topic;

    private CommentDto parentComment;
}
