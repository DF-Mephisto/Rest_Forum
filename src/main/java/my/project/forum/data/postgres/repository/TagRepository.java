package my.project.forum.data.postgres.repository;

import my.project.forum.data.postgres.entity.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {

    Optional<Tag> findByName(String name);

}
