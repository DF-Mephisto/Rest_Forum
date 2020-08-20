package my.project.forum.repository;

import my.project.forum.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends PagingAndSortingRepository<Topic, Long> {

    Page<Topic> findAllBySection_Id(Long section_id, Pageable pageable);

}
