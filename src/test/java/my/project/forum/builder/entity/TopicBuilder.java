package my.project.forum.builder.entity;

import my.project.forum.entity.Section;
import my.project.forum.entity.Tag;
import my.project.forum.entity.Topic;
import my.project.forum.entity.User;

import java.time.LocalDateTime;
import java.util.Set;

public class TopicBuilder {

    private Long id = 0L;
    private String name = "Spring Framework";
    private LocalDateTime placedAt = LocalDateTime.of(2020, 8, 28, 12, 0);;
    private Long views = 0L;
    private User user = new UserBuilder().build();
    private Section section = new SectionBuilder().build();
    private Set<Tag> tags = Set.of(new TagBuilder().build());

    public TopicBuilder id(Long id)
    {
        this.id = id;
        return this;
    }

    public TopicBuilder name(String name)
    {
        this.name = name;
        return this;
    }

    public TopicBuilder placedAt(LocalDateTime placedAt)
    {
        this.placedAt = placedAt;
        return this;
    }

    public TopicBuilder views(Long views)
    {
        this.views = views;
        return this;
    }

    public TopicBuilder user(User user)
    {
        this.user = user;
        return this;
    }

    public TopicBuilder section(Section section)
    {
        this.section = section;
        return this;
    }

    public TopicBuilder tags(Set<Tag> tags)
    {
        this.tags = tags;
        return this;
    }

    public Topic build()
    {
        return new Topic(id, name, placedAt, views, user, section, tags);
    }

}
