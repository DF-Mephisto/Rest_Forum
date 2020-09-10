package my.project.forum.web;

import my.project.forum.aop.annotation.Loggable;
import my.project.forum.data.postgres.dto.CommentDto;
import my.project.forum.data.postgres.entity.Comment;
import my.project.forum.data.postgres.entity.Like;
import my.project.forum.data.postgres.entity.User;
import my.project.forum.error.ActionNotAllowed;
import my.project.forum.error.ItemNotFoundException;
import my.project.forum.data.postgres.patch.CommentPatch;
import my.project.forum.data.postgres.repository.CommentRepository;
import my.project.forum.data.postgres.repository.LikeRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

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
    @Loggable(method = "get", controller = "comment")
    public Page<Comment> getComments(@RequestParam(value = "page", defaultValue = "0") int page)
    {
        Pageable pageable = PageRequest.of(page, props.getCommentsPageSize(),
                Sort.by(Sort.Direction.ASC, "placedAt"));

        return commentRepo.findAll(pageable);
    }

    @PostMapping
    @Loggable(method = "post", controller = "comment")
    public ResponseEntity<Object> newComment(@Valid @RequestBody CommentDto commentDto,
                                             @AuthenticationPrincipal User user) {

        Comment comment = commentDtoToComment(commentDto);

        if (comment.getParentComment() != null)
        {
            Comment parent = commentRepo.findById(comment.getParentComment().getId())
                    .orElseThrow(() -> new ItemNotFoundException("Parent comment with id " +
                            comment.getParentComment().getId() + " doesn't exist"));

            if (!parent.getTopic().getId().equals(comment.getTopic().getId()))
                throw new ItemNotFoundException("Invalid parent comment");
        }

        comment.setUser(user);
        Comment savedComment = commentRepo.save(comment);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedComment.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    @Loggable(method = "get", controller = "comment")
    public Comment getComment(@PathVariable Long id)
    {
        return commentRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Comment with id " + id + " doesn't exist"));
    }

    @PatchMapping("/{id}")
    @Loggable(method = "patch", controller = "comment")
    public Comment updateComment(@Valid @RequestBody CommentPatch patch,
                                 @PathVariable Long id,
                                 @AuthenticationPrincipal User user) {

        Comment comment = commentRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Comment with id " + id + " doesn't exist"));

        boolean hasRoleAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean sameUser = (comment.getUser() != null) && user.getId().equals(comment.getUser().getId());

        if (!hasRoleAdmin && !sameUser)
            throw new ActionNotAllowed("Access denied");

        if (patch.getText() != null)
        {
            if (patch.getText().isBlank())
                throw new ActionNotAllowed("Message mustn't be blank");

            comment.setText(patch.getText());
        }

        return commentRepo.save(comment);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Loggable(method = "delete", controller = "comment")
    public void deleteComment(@PathVariable Long id,
                              @AuthenticationPrincipal User user) {
        Comment comment = commentRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Comment with id " + id + " doesn't exist"));

        boolean hasRoleAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean sameUser = (comment.getUser() != null) && user.getId().equals(comment.getUser().getId());

        if (!hasRoleAdmin && !sameUser)
            throw new ActionNotAllowed("Access denied");

        commentRepo.deleteById(id);
    }

    @GetMapping("/{id}/likes")
    @Loggable(method = "get", controller = "comment")
    public Iterable<Like> getLikes(@PathVariable Long id)
    {
        if (commentRepo.findById(id).isEmpty())
            throw new ItemNotFoundException("Comment with id " + id + " doesn't exist");

        return likeRepo.findAllByCommentId(id);
    }

    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}/likes")
    @Loggable(method = "delete", controller = "comment")
    public void deleteLike(@PathVariable Long id,
                           @AuthenticationPrincipal User user) {
        likeRepo.deleteByCommentIdAndUserId(id, user.getId());

    }

    private Comment commentDtoToComment(CommentDto commentDto)
    {
        ModelMapper modelMapper = new ModelMapper();

        return modelMapper.map(commentDto, Comment.class);
    }
}
