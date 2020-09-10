package my.project.forum.data.postgres.patch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePatch {

    @Size(min=1, max=20, message = "Role name must be between 1 and 20 in length")
    private String name;

    @Min(value = 0x000000, message = "Wrong color value")
    @Max(value = 0xFFFFFF, message = "Wrong color value")
    private Integer color;

}
