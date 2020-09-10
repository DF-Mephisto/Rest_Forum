package my.project.forum.data.postgres.repository;

import my.project.forum.data.postgres.entity.Section;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionRepository extends PagingAndSortingRepository<Section, Long> {

}
