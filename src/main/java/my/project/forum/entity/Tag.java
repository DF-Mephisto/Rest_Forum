package my.project.forum.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="tag", schema = "public")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Tag name can't be empty")
    @Size(min=1, max=20, message = "Tag name must be between 1 and 20 in length")
    private String name;

    @ManyToMany
    @JoinTable(name = "topic_tag",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id"))
    private List<Topic> topics;

    public Tag(){
        topics = new ArrayList<>();
    }
}
