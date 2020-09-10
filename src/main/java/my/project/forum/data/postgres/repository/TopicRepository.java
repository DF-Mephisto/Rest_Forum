package my.project.forum.data.postgres.repository;

import my.project.forum.data.postgres.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends PagingAndSortingRepository<Topic, Long> {

    @Query(value = "SELECT res.id, res.name, res.placed_at, res.views, res.section_id, res.user_id " +
                    "FROM ( " +
                        "SELECT t.id, t.name, t.placed_at, t.views, t.section_id, t.user_id, c.placed_at AS last_comment_date, " +
                        "row_number() over (partition by t.id order by c.placed_at desc) AS num " +
                        "FROM gen.topic t " +
                        "LEFT JOIN gen.comment c ON t.id = c.topic_id " +
                        "WHERE t.section_id = :sectionId " +
                    ") AS res " +
                    "WHERE res.num = 1 " +
                    "ORDER BY " +
                    "CASE WHEN res.last_comment_date IS NULL THEN 1 ELSE 0 END, " +
                    "res.last_comment_date DESC",
            countQuery = "SELECT COUNT(*) " +
                            "FROM gen.topic t " +
                            "WHERE t.section_id = :sectionId",
            nativeQuery = true)
    Page<Topic> findAllBySection_Id(@Param("sectionId") Long section_id, Pageable pageable);
}
