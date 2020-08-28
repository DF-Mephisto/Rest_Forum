package my.project.forum.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.project.forum.entity.Role;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Serializable {

    private Long id;

    @NotBlank(message = "Username can't be empty")
    @Size(min=4, max=20, message = "Name must be between 4 and 20 in length")
    private String username;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must be minimum 8 characters in length and include at least one digit, lower-case, " +
                    "upper-case and special characters and mustn't include any space symbols")
    @NotNull(message = "Password can't be null")
    private String password;

    @Size(max=1000, message = "Information mustn't be longer than 1000 in length")
    private String information;

    @Email(message = "Wrong email")
    @NotNull(message = "Email can't be null")
    private String email;

    private byte[] avatar;

    private Role role;
}
