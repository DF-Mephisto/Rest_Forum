package my.project.forum.repository;

import my.project.forum.entity.Section;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionRepository extends PagingAndSortingRepository<Section, Long> {

}
