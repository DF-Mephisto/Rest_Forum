package my.project.forum.patch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.project.forum.entity.Role;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfilePatch {

    @Size(min=4, max=20, message = "Name must be between 4 and 20 in length")
    private String username;

    @Size(max=1000, message = "Information mustn't be longer than 1000 in length")
    private String information;

    @Email(message = "Wrong email")
    private String email;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must be minimum 8 characters in length and include at least one digit, lower-case" +
                    "upper-case and special characters and mustn't include any space symbols")
    private String password;

    private String oldPassword;

    private byte[] avatar;

    private Role role;

}
