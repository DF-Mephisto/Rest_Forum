package my.project.forum.web;

import my.project.forum.dto.TopicDto;
import my.project.forum.entity.Comment;
import my.project.forum.entity.Topic;
import my.project.forum.entity.User;
import my.project.forum.error.ActionNotAllowed;
import my.project.forum.error.ItemNotFoundException;
import my.project.forum.patch.TopicPatch;
import my.project.forum.repository.CommentRepository;
import my.project.forum.repository.TopicRepository;
import my.project.forum.service.Properties;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<Object> newTopic(@Valid @RequestBody TopicDto topicDto,
                                           @AuthenticationPrincipal User user) {

        Topic topic = topicDtoToTopic(topicDto);

        topic.setUser(user);
        topic.setViews(0L);
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

    @PatchMapping("/{id}")
    public Topic updateTopic(@Valid @RequestBody TopicPatch patch, @PathVariable Long id) {

        Topic patchedTopic = topicRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Topic with id " + id + " doesn't exist"));

        if (patch.getName() != null)
        {
            if (patch.getName().isBlank())
                throw new ActionNotAllowed("Topic name mustn't be blank");

            patchedTopic.setName(patch.getName());
        }

        if (patch.getTags() != null)
            patchedTopic.setTags(patch.getTags());

        return topicRepo.save(patchedTopic);
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

    private Topic topicDtoToTopic(TopicDto topicDto)
    {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(topicDto, Topic.class);
    }
}
