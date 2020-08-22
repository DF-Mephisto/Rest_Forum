package my.project.forum.patch;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class TagPatch {

    @Size(min=1, max=20, message = "Tag name must be between 1 and 20 in length")
    private String name;

}
