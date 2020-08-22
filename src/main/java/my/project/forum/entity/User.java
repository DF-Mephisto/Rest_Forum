package my.project.forum.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

@Data
@Entity
@Table(name="user_info")
@JsonIgnoreProperties(value = "password", allowSetters = true)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Username can't be empty")
    @Size(min=4, max=20, message = "Name must be between 4 and 20 in length")
    @Column(unique = true, nullable = false)
    private String username;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must be minimum 8 characters in length and include at least one digit, lower-case" +
                      "upper-case and special characters and mustn't include any space symbols")
    @NotNull(message = "Password can't be null")
    private String password;

    @Size(max=1000, message = "Information mustn't be longer than 1000 in length")
    private String information;

    @Email(message = "Wrong email")
    @NotNull(message = "Email can't be null")
    private String email;

    @Column(name="registration_date", nullable = false)
    private LocalDate registrationDate;

    private byte[] avatar;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name="non_locked", nullable = false)
    private boolean nonLocked;

    public User()
    {
        nonLocked = true;
    }

    @PrePersist
    public void registeredAt()
    {
        setRegistrationDate(LocalDate.now());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        if (role == null)
            return new HashSet<>();
        else
            return Arrays.asList(new SimpleGrantedAuthority(this.role.getName()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return nonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
