package my.project.forum.builder.dto;

import my.project.forum.builder.entity.TagBuilder;
import my.project.forum.dto.SectionDto;
import my.project.forum.dto.TopicDto;
import my.project.forum.entity.Tag;

import java.util.Set;

public class TopicDtoBuilder {

    private Long id = 0L;
    private String name = "Spring Framework";
    private SectionDto section = new SectionDtoBuilder().build();
    private Set<Tag> tags = Set.of(new TagBuilder().build());

    public TopicDtoBuilder id(Long id)
    {
        this.id = id;
        return this;
    }

    public TopicDtoBuilder name(String name)
    {
        this.name = name;
        return this;
    }

    public TopicDtoBuilder section(SectionDto section)
    {
        this.section = section;
        return this;
    }

    public TopicDtoBuilder tags(Set<Tag> tags)
    {
        this.tags = tags;
        return this;
    }

    public TopicDto build()
    {
        return new TopicDto(id, name, section, tags);
    }

}
