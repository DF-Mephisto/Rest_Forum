package my.project.forum.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;

@Data
@Entity
@Table(name="role")
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Role name can't be empty")
    @Size(min=1, max=20, message = "Role name must be between 1 and 20 in length")
    @Column(unique = true, nullable = false)
    private String name;

    @Min(value = 0x000000, message = "Wrong color value")
    @Max(value = 0xFFFFFF, message = "Wrong color value")
    @NotNull(message = "Color can't be null")
    private Integer color;

}
