package my.project.forum.patch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionPatch {

    @Size(min=1, max=100, message = "Section name must be between 1 and 100 in length")
    private String name;

}
