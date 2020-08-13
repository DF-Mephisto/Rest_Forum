package my.project.forum.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name="comment", schema = "public")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Comment can't be empty")
    @Size(min=1, max=1000, message = "Comment must be between 1 and 1000 in length")
    private String text;

    @Column(name="placed_at")
    private LocalDateTime placedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes;

    public Comment()
    {
        likes = new ArrayList<>();
    }

    @PrePersist
    public void createdAt()
    {
        setPlacedAt(LocalDateTime.now());
    }
}
