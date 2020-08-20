package my.project.forum.web;

import my.project.forum.entity.Comment;
import my.project.forum.error.ItemNotFoundException;
import my.project.forum.repository.CommentRepository;
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
@RequestMapping("/comments")
public class CommentController {

    private CommentRepository commentRepo;
    private Properties props;

    @Autowired
    public CommentController(CommentRepository commentRepo,
                           Properties props)
    {
        this.commentRepo = commentRepo;
        this.props = props;
    }

    @GetMapping(produces = "application/json")
    public Page<Comment> getComments(@RequestParam(value = "page", defaultValue = "0") int page)
    {
        Pageable pageable = PageRequest.of(page, props.getCommentsPageSize(),
                Sort.by(Sort.Direction.ASC, "placedAt"));

        return commentRepo.findAll(pageable);
    }

    @PostMapping
    public ResponseEntity<Object> newComment(@Valid @RequestBody Comment comment) {
        if (comment.getParentComment() != null)
        {
            Comment parent = commentRepo.findById(comment.getParentComment().getId())
                    .orElseThrow(() -> new ItemNotFoundException("Parent comment with id " +
                            comment.getParentComment().getId() + " doesn't exist"));

            if (!parent.getTopic().getId().equals(comment.getTopic().getId()))
                throw new ItemNotFoundException("Invalid parent comment");
        }

        Comment savedComment = commentRepo.save(comment);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedComment.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        commentRepo.deleteById(id);
    }
}
