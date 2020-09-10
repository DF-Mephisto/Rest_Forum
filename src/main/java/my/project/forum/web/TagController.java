package my.project.forum.web;

import my.project.forum.aop.annotation.Loggable;
import my.project.forum.data.postgres.entity.Tag;
import my.project.forum.error.ActionNotAllowed;
import my.project.forum.error.ItemAlreadyExistsException;
import my.project.forum.error.ItemNotFoundException;
import my.project.forum.data.postgres.patch.TagPatch;
import my.project.forum.data.postgres.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/tag")
public class TagController {

    private TagRepository tagRepo;

    @Autowired
    public TagController(TagRepository tagRepo)
    {
        this.tagRepo = tagRepo;
    }

    @GetMapping(produces = "application/json")
    @Loggable(method = "get", controller = "tag")
    public Iterable<Tag> getAllTags()
    {
        return tagRepo.findAll();
    }

    @PostMapping
    @Loggable(method = "post", controller = "tag")
    public ResponseEntity<Object> newTag(@Valid @RequestBody Tag tag) {
        if (tagRepo.findByName(tag.getName()).isPresent())
            throw new ItemAlreadyExistsException("Tag with name " + tag.getName() + " already exists");

        Tag savedTag = tagRepo.save(tag);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedTag.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    @Loggable(method = "get", controller = "tag")
    public Tag getTag(@PathVariable Long id)
    {
        return tagRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Tag with id " + id + " doesn't exist"));
    }

    @PatchMapping("/{id}")
    @Loggable(method = "patch", controller = "tag")
    public Tag updateTag(@Valid @RequestBody TagPatch patch, @PathVariable Long id) {

        Tag patchedTag = tagRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Tag with id " + id + " doesn't exist"));

        if (patch.getName() != null)
        {
            if (patch.getName().isBlank())
                throw new ActionNotAllowed("Tag name mustn't be blank");

            if (!patchedTag.getName().equals(patch.getName()) && tagRepo.findByName(patch.getName()).isPresent())
                throw new ItemAlreadyExistsException("Tag with name " + patch.getName() + " already exists");

            patchedTag.setName(patch.getName());
        }

        return tagRepo.save(patchedTag);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Loggable(method = "delete", controller = "tag")
    public void deleteTag(@PathVariable Long id) {
        tagRepo.deleteById(id);
    }
}
