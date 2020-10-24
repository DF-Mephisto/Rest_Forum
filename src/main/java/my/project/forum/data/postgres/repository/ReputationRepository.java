package my.project.forum.data.postgres.repository;

import my.project.forum.data.postgres.entity.Reputation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReputationRepository extends CrudRepository<Reputation, Long> {

    Iterable<Reputation> findAllByTargetId(Long target_user_id);

}
