package my.project.forum.data.builder.entity;

import my.project.forum.data.postgres.entity.Role;

public class RoleBuilder {

    private Long id = 0L;
    private String name = "ROLE_ADMIN";
    private Integer color = 0x00FF00;

    public RoleBuilder id(Long id)
    {
        this.id = id;
        return this;
    }

    public RoleBuilder name(String name)
    {
        this.name = name;
        return this;
    }

    public RoleBuilder color(Integer color)
    {
        this.color = color;
        return this;
    }

    public Role build()
    {
        return new Role(id, name, color);
    }
}
