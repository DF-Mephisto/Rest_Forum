package my.project.forum.data.postgres.repository;

import my.project.forum.data.postgres.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends PagingAndSortingRepository<Comment, Long> {

    Page<Comment> findAllByTopic_Id(Long topic_id, Pageable pageable);

}
