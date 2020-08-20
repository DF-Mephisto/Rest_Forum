package my.project.forum.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    @NotBlank(message = "Message can't be empty")
    @Size(min=1, max=1000, message = "Comment must be between 1 and 1000 in length")
    @Column(nullable = false)
    private String text;

    @Column(name="placed_at", nullable = false)
    private LocalDateTime placedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    @NotNull(message = "Parent topic can't be null")
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE)
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
