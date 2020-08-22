package my.project.forum.patch;

import lombok.Data;
import my.project.forum.entity.Tag;

import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class TopicPatch {

    @Size(min=1, max=100, message = "Topic name must be between 1 and 100 in length")
    private String name;

    private Set<Tag> tags;

}
