package my.project.forum.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name="like")
@NoArgsConstructor
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    @NotNull(message = "User can't be null")
    private User user;

    @ManyToOne
    @JoinColumn(name="comment_id")
    @NotNull(message = "Comment can't be null")
    private Comment comment;

}
