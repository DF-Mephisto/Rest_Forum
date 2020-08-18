package my.project.forum.repository;

import my.project.forum.entity.Role;
import my.project.forum.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = :name AND u.id <> :id")
    Optional<User> findByUsernameExceptForId(String name, Long id);

    Optional<User> findByUsername(String username);
    Optional<User> findByRole(Role role);
}
