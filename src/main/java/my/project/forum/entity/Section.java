package my.project.forum.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="section", schema = "public")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Section name can't be empty")
    @Size(min=1, max=100, message = "Section name must be between 1 and 100 in length")
    private String name;

    @Column(name="placed_at")
    private LocalDateTime placedAt;

    @PrePersist
    public void createdAt()
    {
        setPlacedAt(LocalDateTime.now());
    }
}
