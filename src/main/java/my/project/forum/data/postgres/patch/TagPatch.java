package my.project.forum.data.postgres.patch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagPatch {

    @Size(min=1, max=20, message = "Tag name must be between 1 and 20 in length")
    private String name;

}
