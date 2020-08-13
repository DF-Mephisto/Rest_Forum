package my.project.forum.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="like", schema = "public")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;
}
