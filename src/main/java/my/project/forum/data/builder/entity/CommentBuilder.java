package my.project.forum.data.builder.entity;

import my.project.forum.data.postgres.entity.*;

import java.time.LocalDateTime;

public class CommentBuilder {

    private Long id = 0L;
    private String text = "Hello World!";
    private LocalDateTime placedAt = LocalDateTime.of(2020, 8, 28, 12, 0);
    private User user = new UserBuilder().build();
    private Topic topic = new TopicBuilder().build();
    private Comment parentComment = null;

    public CommentBuilder id(Long id)
    {
        this.id = id;
        return this;
    }

    public CommentBuilder text(String text)
    {
        this.text = text;
        return this;
    }

    public CommentBuilder placedAt(LocalDateTime placedAt)
    {
        this.placedAt = placedAt;
        return this;
    }

    public CommentBuilder user(User user)
    {
        this.user = user;
        return this;
    }

    public CommentBuilder topic(Topic topic)
    {
        this.topic = topic;
        return this;
    }

    public CommentBuilder parentComment(Comment parentComment)
    {
        this.parentComment = parentComment;
        return this;
    }

    public Comment build()
    {
        return new Comment(id, text, placedAt, user, topic, parentComment);
    }

}
