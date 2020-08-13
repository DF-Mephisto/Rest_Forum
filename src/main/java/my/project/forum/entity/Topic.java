package my.project.forum.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
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
        views = 0L;
        tags = new ArrayList<>();
    }

    @PrePersist
    public void createdAt()
    {
        setPlacedAt(LocalDateTime.now());
    }
}
