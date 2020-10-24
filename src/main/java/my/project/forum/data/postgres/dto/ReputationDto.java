package my.project.forum.data.postgres.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReputationDto implements Serializable {

    private Long id;

    @Size(max=100, message = "Reputation message must be no longer than 100 characters")
    private String msg;

    @NotNull(message = "Target user can't be null")
    private UserDto target;

}
