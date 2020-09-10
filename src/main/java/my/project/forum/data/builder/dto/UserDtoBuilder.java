package my.project.forum.data.builder.dto;

import my.project.forum.data.builder.entity.RoleBuilder;
import my.project.forum.data.postgres.dto.UserDto;
import my.project.forum.data.postgres.entity.Role;

public class UserDtoBuilder {

    private Long id = 0L;
    private String username = "test user";
    private String password = "tT1#1111";
    private String information = "user info";
    private String email = "test@test.com";
    private byte[] avatar = {1, 2, 3};
    private Role role = new RoleBuilder().id(0L).name("ROLE_USER").color(0xFFFFFF).build();

    public UserDtoBuilder id(Long id)
    {
        this.id = id;
        return this;
    }

    public UserDtoBuilder username(String username)
    {
        this.username = username;
        return this;
    }

    public UserDtoBuilder password(String password)
    {
        this.password = password;
        return this;
    }

    public UserDtoBuilder information(String information)
    {
        this.information = information;
        return this;
    }

    public UserDtoBuilder email(String email)
    {
        this.email = email;
        return this;
    }

    public UserDtoBuilder avatar(byte[] avatar)
    {
        this.avatar = avatar;
        return this;
    }

    public UserDtoBuilder role(Role role)
    {
        this.role = role;
        return this;
    }

    public UserDto build()
    {
        return new UserDto(id, username, password, information, email, avatar, role);
    }

}
