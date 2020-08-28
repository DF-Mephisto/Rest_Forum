package my.project.forum.builder.entity;

import my.project.forum.entity.Comment;
import my.project.forum.entity.Like;
import my.project.forum.entity.User;

public class LikeBuilder {

    private Long id = 0L;
    private User user = new UserBuilder().build();
    private Comment comment = new CommentBuilder().build();

    public LikeBuilder id(Long id)
    {
        this.id = id;
        return this;
    }

    public LikeBuilder user(User user)
    {
        this.user = user;
        return this;
    }

    public LikeBuilder comment(Comment comment)
    {
        this.comment = comment;
        return this;
    }

    public Like build()
    {
        return new Like(id, user, comment);
    }

}
