package my.project.forum.data.postgres.repository;

import my.project.forum.data.postgres.entity.Role;
import my.project.forum.data.postgres.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByRole(Role role);
}
