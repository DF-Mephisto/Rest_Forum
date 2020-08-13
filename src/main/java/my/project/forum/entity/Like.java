package my.project.forum.entity;

import javax.persistence.*;

@Entity
@IdClass(LikeId.class)
@Table(name="like", schema = "public")
public class Like {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    public Like(){}

    public Like(User user, Comment comment)
    {
        this.user = user;
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
