package my.project.forum.entity;

import java.io.Serializable;
import java.util.Objects;

public class LikeId implements Serializable {

    private User user;
    private Comment comment;

    public LikeId(){}

    public LikeId(User user, Comment comment)
    {
        this.user = user;
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikeId likeId = (LikeId) o;
        return Objects.equals(user, likeId.user) &&
                Objects.equals(comment, likeId.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, comment);
    }
}
