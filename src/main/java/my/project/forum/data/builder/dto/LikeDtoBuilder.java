package my.project.forum.data.builder.dto;

import my.project.forum.data.postgres.dto.CommentDto;
import my.project.forum.data.postgres.dto.LikeDto;

public class LikeDtoBuilder {

    private Long id = 0L;
    private CommentDto comment = new CommentDtoBuilder().build();

    public LikeDtoBuilder id(Long id)
    {
        this.id = id;
        return this;
    }

    public LikeDtoBuilder comment(CommentDto comment)
    {
        this.comment = comment;
        return this;
    }

    public LikeDto build()
    {
        return new LikeDto(id, comment);
    }

}
