package my.project.forum.builder;

import my.project.forum.entity.Tag;

public class TagBuilder {

    private Long id = 0L;
    private String name = "Java";

    public TagBuilder id(Long id)
    {
        this.id = id;
        return this;
    }

    public TagBuilder name(String name)
    {
        this.name = name;
        return this;
    }

    public Tag build()
    {
        return new Tag(id, name);
    }

}
