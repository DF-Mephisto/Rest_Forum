package my.project.forum.data.postgres.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import my.project.forum.data.postgres.entity.Tag;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class TopicDto implements Serializable {

    private Long id;

    @NotBlank(message = "Topic name can't be empty")
    @Size(min=1, max=100, message = "Topic name must be between 1 and 100 in length")
    private String name;

    @NotNull(message = "Parent section can't be null")
    private SectionDto section;

    private Set<Tag> tags;

    public TopicDto(){
        tags = new LinkedHashSet<>();
    }
}
