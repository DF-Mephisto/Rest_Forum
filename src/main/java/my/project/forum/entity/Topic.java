package my.project.forum.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name="topic")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Topic name can't be empty")
    @Size(min=1, max=100, message = "Topic name must be between 1 and 100 in length")
    @Column(nullable = false)
    private String name;

    @Column(name="placed_at", nullable = false)
    private LocalDateTime placedAt;

    @Column(nullable = false)
    private Long views;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @ManyToMany(mappedBy = "topics")
    private Set<Tag> tags;

    public Topic(){
        views = 0L;
        tags = new LinkedHashSet<>();
    }

    @PrePersist
    public void createdAt()
    {
        setPlacedAt(LocalDateTime.now());
    }
}
