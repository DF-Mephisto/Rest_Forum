package my.project.forum.builder.entity;

import my.project.forum.entity.Section;
import java.time.LocalDateTime;

public class SectionBuilder {

    private Long id = 0L;
    private String name = "Programming";
    private LocalDateTime placedAt = LocalDateTime.of(2020, 8, 28, 12, 0);

    public SectionBuilder id(Long id)
    {
        this.id = id;
        return this;
    }

    public SectionBuilder name(String name)
    {
        this.name = name;
        return this;
    }

    public SectionBuilder placedAt(LocalDateTime placedAt)
    {
        this.placedAt = placedAt;
        return this;
    }

    public Section build()
    {
        return new Section(id, name, placedAt);
    }
}
