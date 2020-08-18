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
@Table(name="comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Comment can't be empty")
    @Size(min=1, max=1000, message = "Comment must be between 1 and 1000 in length")
    @Column(nullable = false)
    private String text;

    @Column(name="placed_at", nullable = false)
    private LocalDateTime placedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment comment;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes;

    @OneToMany(mappedBy = "comment", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Comment> comments;

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
