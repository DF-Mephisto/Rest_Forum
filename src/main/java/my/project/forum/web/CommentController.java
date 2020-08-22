package my.project.forum.web;

import my.project.forum.entity.Comment;
import my.project.forum.entity.Like;
import my.project.forum.error.ItemNotFoundException;
import my.project.forum.repository.CommentRepository;
import my.project.forum.repository.LikeRepository;
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
import java.util.Optional;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private CommentRepository commentRepo;
    private LikeRepository likeRepo;
    private Properties props;

    @Autowired
    public CommentController(CommentRepository commentRepo,
                             LikeRepository likeRepo,
                             Properties props)
    {
        this.commentRepo = commentRepo;
        this.likeRepo = likeRepo;
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

    @GetMapping("/{id}")
    public Comment getComment(@PathVariable Long id)
    {
        return commentRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Comment with id " + id + " doesn't exist"));
    }

    @PutMapping("/{id}")
    public Comment updateComment(@Valid @RequestBody Comment comment, @PathVariable Long id) {

        Comment savedComment = commentRepo.findById(id)
                .map(x -> {
                    x.setText(comment.getText());
                    return commentRepo.save(x);
                })
                .orElseThrow(() -> new ItemNotFoundException("Comment with id " + id + " doesn't exist"));

        return savedComment;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        commentRepo.deleteById(id);
    }

    @GetMapping("/{id}/likes")
    public Iterable<Like> getLikes(@PathVariable Long id)
    {
        if (commentRepo.findById(id).isEmpty())
            throw new ItemNotFoundException("Comment with id " + id + " doesn't exist");

        return likeRepo.findAllByCommentId(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}/likes")
    public void deleteLike(@PathVariable Long id) {
        //Optional<Like> like = likeRepo.findByCommentIdAndUserId(id)

    }
}
