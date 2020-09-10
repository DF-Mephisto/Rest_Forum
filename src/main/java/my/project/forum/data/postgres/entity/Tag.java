package my.project.forum.data.postgres.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Data
@Entity
@Table(name="tag")
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Tag name can't be empty")
    @Size(min=1, max=20, message = "Tag name must be between 1 and 20 in length")
    @Column(unique = true, nullable = false)
    private String name;

}
