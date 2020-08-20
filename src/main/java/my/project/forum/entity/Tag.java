package my.project.forum.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Data
@Entity
@Table(name="tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Tag name can't be empty")
    @Size(min=1, max=20, message = "Tag name must be between 1 and 20 in length")
    @Column(unique = true, nullable = false)
    private String name;

}
