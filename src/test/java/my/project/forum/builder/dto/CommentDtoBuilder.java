package my.project.forum.builder.dto;

import my.project.forum.dto.CommentDto;
import my.project.forum.dto.TopicDto;

public class CommentDtoBuilder {

    private Long id = 0L;
    private String text = "Hello World";
    private TopicDto topic = new TopicDtoBuilder().build();
    private CommentDto parentComment = null;

    public CommentDtoBuilder id(Long id)
    {
        this.id = id;
        return this;
    }

    public CommentDtoBuilder text(String text)
    {
        this.text = text;
        return this;
    }

    public CommentDtoBuilder topic(TopicDto topic)
    {
        this.topic = topic;
        return this;
    }

    public CommentDtoBuilder parentComment(CommentDto parentComment)
    {
        this.parentComment = parentComment;
        return this;
    }

    public CommentDto build()
    {
        return new CommentDto(id, text, topic, parentComment);
    }
}
