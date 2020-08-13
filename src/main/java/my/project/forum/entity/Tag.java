package my.project.forum.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

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

    public Tag(String name)
    {
        this();
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void addTopic(Topic topic)
    {
        topics.add(topic);
    }

    public void removeTopic(Topic topic)
    {
        topics.remove(topic);
    }
}
