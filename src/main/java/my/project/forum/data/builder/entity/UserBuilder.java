package my.project.forum.data.builder.entity;

import my.project.forum.data.postgres.entity.Role;
import my.project.forum.data.postgres.entity.User;

import java.time.LocalDate;

public class UserBuilder {

    private Long id = 0L;
    private String username = "test user";
    private String password = "tT1#1111";
    private String information = "user info";
    private String email = "test@test.com";
    private LocalDate registrationDate = LocalDate.of(2020, 8, 27);
    private byte[] avatar = {1, 2, 3};
    private Role role = new RoleBuilder().id(0L).name("ROLE_USER").color(0xFFFFFF).build();
    private boolean nonLocked = true;

    public UserBuilder id(Long id)
    {
        this.id = id;
        return this;
    }

    public UserBuilder username(String username)
    {
        this.username = username;
        return this;
    }

    public UserBuilder password(String password)
    {
        this.password = password;
        return this;
    }

    public UserBuilder information(String information)
    {
        this.information = information;
        return this;
    }

    public UserBuilder email(String email)
    {
        this.email = email;
        return this;
    }

    public UserBuilder registrationDate(LocalDate registrationDate)
    {
        this.registrationDate = registrationDate;
        return this;
    }

    public UserBuilder avatar(byte[] avatar)
    {
        this.avatar = avatar;
        return this;
    }

    public UserBuilder role(Role role)
    {
        this.role = role;
        return this;
    }

    public UserBuilder nonLocked(boolean nonLocked)
    {
        this.nonLocked = nonLocked;
        return this;
    }

    public User build()
    {
        return new User(id, username, password, information, email, registrationDate, avatar, role, nonLocked);
    }

}
