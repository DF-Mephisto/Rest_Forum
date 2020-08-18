package my.project.forum.repository;

import my.project.forum.entity.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

    @Query("SELECT r FROM Role r WHERE r.name = :name AND r.id <> :id")
    Optional<Role> findByNameExceptForId(String name, Long id);

    Optional<Role> findByName(String name);
}
