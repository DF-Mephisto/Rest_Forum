package my.project.forum.repository;

import my.project.forum.entity.Like;
import my.project.forum.entity.LikeId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends CrudRepository<Like, LikeId> {
}
