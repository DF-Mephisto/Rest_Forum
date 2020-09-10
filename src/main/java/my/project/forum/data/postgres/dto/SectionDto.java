package my.project.forum.data.postgres.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionDto implements Serializable {

    private Long id;

    @NotBlank(message = "Section name can't be empty")
    @Size(min=1, max=100, message = "Section name must be between 1 and 100 in length")
    private String name;

}
