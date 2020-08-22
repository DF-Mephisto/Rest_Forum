package my.project.forum.repository;

import my.project.forum.entity.Like;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends CrudRepository<Like, Long> {

    Iterable<Like> findAllByCommentId(Long comment_id);
    Optional<Like> findByCommentIdAndUserId(Long comment_id, Long user_id);
}
