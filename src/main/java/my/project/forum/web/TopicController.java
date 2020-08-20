package my.project.forum.web;

import my.project.forum.entity.Comment;
import my.project.forum.entity.Topic;
import my.project.forum.error.ItemNotFoundException;
import my.project.forum.repository.CommentRepository;
import my.project.forum.repository.TopicRepository;
import my.project.forum.service.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/topics")
public class TopicController {

    private TopicRepository topicRepo;
    private CommentRepository commentRepo;
    private Properties props;

    @Autowired
    public TopicController(TopicRepository topicRepo,
                           CommentRepository commentRepo,
                           Properties props)
    {
        this.topicRepo = topicRepo;
        this.commentRepo = commentRepo;
        this.props = props;
    }

    @GetMapping(produces = "application/json")
    public Page<Topic> getTopics(@RequestParam(value = "page", defaultValue = "0") int page)
    {
        Pageable pageable = PageRequest.of(page, props.getTopicsPageSize(),
                Sort.by(Sort.Direction.DESC, "placedAt"));

        return topicRepo.findAll(pageable);
    }

    @PostMapping
    public ResponseEntity<Object> newTopic(@Valid @RequestBody Topic topic) {

        Topic savedTopic = topicRepo.save(topic);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedTopic.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public Topic getTopic(@PathVariable Long id)
    {
        Topic t = topicRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Topic with id " + id + " doesn't exist"));

        t.incrementViews();
        topicRepo.save(t);

        return t;
    }

    @PutMapping("/{id}")
    public Topic updateTopic(@Valid @RequestBody Topic topic, @PathVariable Long id) {

        Topic savedTopic = topicRepo.findById(id)
                .map(x -> {
                    x.setName(topic.getName());
                    x.setTags(topic.getTags());
                    return topicRepo.save(x);
                })
                .orElseThrow(() -> new ItemNotFoundException("Topic with id " + id + " doesn't exist"));

        return savedTopic;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteTopic(@PathVariable Long id) {
        topicRepo.deleteById(id);
    }

    @GetMapping("/{id}/comments")
    public Page<Comment> getComments(@PathVariable Long id,
                                     @RequestParam(value = "page", defaultValue = "0") int page)
    {
        if (topicRepo.findById(id).isEmpty())
            throw new ItemNotFoundException("Topic with id " + id + " doesn't exist");

        Pageable pageable = PageRequest.of(page, props.getCommentsPageSize(),
                Sort.by(Sort.Direction.DESC, "placedAt"));

        return commentRepo.findAllByTopic_Id(id, pageable);
    }
}
