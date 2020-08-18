package my.project.forum.web;

import my.project.forum.entity.Tag;
import my.project.forum.error.ItemAlreadyExistsException;
import my.project.forum.error.ItemNotFoundException;
import my.project.forum.repository.TagRepository;
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
    public Iterable<Tag> getAllTags()
    {
        return tagRepo.findAll();
    }

    @PostMapping
    public ResponseEntity<Object> newTag(@Valid @RequestBody Tag tag) {
        if (tagRepo.findByName(tag.getName()).isPresent())
            throw new ItemAlreadyExistsException("Tag with name " + tag.getName() + " already exists");

        Tag savedTag = tagRepo.save(tag);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedTag.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public Tag getTag(@PathVariable Long id)
    {
        return tagRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Tag with id " + id + " doesn't exist"));
    }

    @PutMapping("/{id}")
    public Tag updateTag(@Valid @RequestBody Tag tag, @PathVariable Long id) {
        if (tagRepo.findByNameExceptForId(tag.getName(), id).isPresent())
            throw new ItemAlreadyExistsException("Tag with name " + tag.getName() + " already exists");

        Tag savedTag = tagRepo.findById(id)
                .map(x -> {
                    x.setName(tag.getName());
                    return tagRepo.save(x);
                })
                .orElseThrow(() -> new ItemNotFoundException("Tag with id " + id + " doesn't exist"));

        return savedTag;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteTag(@PathVariable Long id) {
        tagRepo.deleteById(id);
    }
}
