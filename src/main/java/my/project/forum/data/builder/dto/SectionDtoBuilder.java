package my.project.forum.data.builder.dto;

import my.project.forum.data.postgres.dto.SectionDto;

public class SectionDtoBuilder {

    private Long id = 0L;
    private String name = "Programming";

    public SectionDtoBuilder id(Long id)
    {
        this.id = id;
        return this;
    }

    public SectionDtoBuilder name(String name)
    {
        this.name = name;
        return this;
    }

    public SectionDto build()
    {
        return new SectionDto(id, name);
    }

}
