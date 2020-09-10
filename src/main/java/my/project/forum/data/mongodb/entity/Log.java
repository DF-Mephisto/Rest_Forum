package my.project.forum.data.mongodb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "log")
public class Log {

    @Id
    private String id;

    @Size(min=4, max=20, message = "Name must be between 4 and 20 in length")
    private String username;

    @NotBlank(message = "Description mustn't be blank")
    @Field("description")
    private String desc;
}
