package my.project.forum.data.postgres.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name = "reputation")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reputation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(max=100, message = "Reputation message must be no longer than 100 characters")
    private String msg;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="target_user_id")
    @NotNull(message = "Target user can't be null")
    private User target;
}
