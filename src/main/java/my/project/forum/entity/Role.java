package my.project.forum.entity;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name="role", schema = "public")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Role name can't be empty")
    @Size(min=1, max=20, message = "Role name must be between 1 and 20 in length")
    private String name;

    @Min(value = 0x000000, message = "Wrong color value")
    @Max(value = 0xFFFFFF, message = "Wrong color value")
    private Integer color;

    public Role()
    {

    }

    public Role(String name, Integer color)
    {
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }
}
