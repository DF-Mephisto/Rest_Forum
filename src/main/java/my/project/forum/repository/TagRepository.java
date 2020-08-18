package my.project.forum.repository;

import my.project.forum.entity.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {

    @Query("SELECT t FROM Tag t WHERE t.name = :name AND t.id <> :id")
    Optional<Tag> findByNameExceptForId(String name, Long id);

    Optional<Tag> findByName(String name);

}
