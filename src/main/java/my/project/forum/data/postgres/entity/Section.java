package my.project.forum.data.postgres.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="section")
@NoArgsConstructor
@AllArgsConstructor
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Section name can't be empty")
    @Size(min=1, max=100, message = "Section name must be between 1 and 100 in length")
    @Column(nullable = false)
    private String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name="placed_at", nullable = false)
    private LocalDateTime placedAt;

    @PrePersist
    public void createdAt()
    {
        setPlacedAt(LocalDateTime.now());
    }
}
