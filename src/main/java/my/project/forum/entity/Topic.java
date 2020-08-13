package my.project.forum.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="topic", schema = "public")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Topic name can't be empty")
    @Size(min=1, max=100, message = "Topic name must be between 1 and 100 in length")
    private String name;

    @Column(name="placed_at")
    private LocalDateTime placedAt;

    private Long views;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Topic section;

    @ManyToMany(mappedBy = "topics")
    private List<Tag> tags;

    public Topic(){
        views = 0l;
        tags = new ArrayList<>();
    }

    public Topic(String name)
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

    public LocalDateTime getPlacedAt() {
        return placedAt;
    }

    public void setPlacedAt(LocalDateTime placedAt) {
        this.placedAt = placedAt;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Topic getSection() {
        return section;
    }

    public void setSection(Topic section) {
        this.section = section;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void addTag(Tag tag)
    {
        tags.add(tag);
    }

    public void removeTag(Tag tag)
    {
        tags.remove(tag);
    }

    @PrePersist
    public void createdAt()
    {
        setPlacedAt(LocalDateTime.now());
    }
}
