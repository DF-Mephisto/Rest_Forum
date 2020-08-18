package my.project.forum.web;

import my.project.forum.entity.Section;
import my.project.forum.error.ItemNotFoundException;
import my.project.forum.repository.SectionRepository;
import my.project.forum.service.Properties;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/sections")
public class SectionController {

    private SectionRepository SectionRepo;
    private Properties props;

    @Autowired
    public SectionController(SectionRepository SectionRepo,
                             Properties props)
    {
        this.SectionRepo = SectionRepo;
        this.props = props;
    }

    @GetMapping(produces = "application/json")
    public Iterable<Section> getSections(@RequestParam(value = "page", defaultValue = "0") int page)
    {
        Pageable pageable = PageRequest.of(page, props.getSectionsPageSize(),
                Sort.by(Sort.Direction.ASC, "placedAt"));

        return SectionRepo.findAll(pageable);
    }

    @PostMapping("/add")
    public ResponseEntity<Object> newSection(@Valid @RequestBody Section section) {

        Section savedSection = SectionRepo.save(section);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedSection.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public Section getSections(@PathVariable Long id)
    {
        return SectionRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Section with id " + id + " doesn't exist"));
    }

    @PutMapping("/{id}/edit")
    public Section updateSection(@Valid @RequestBody Section section, @PathVariable Long id) {

        Section savedSection = SectionRepo.findById(id)
                .map(x -> {
                    x.setName(section.getName());
                    return SectionRepo.save(x);
                })
                .orElseThrow(() -> new ItemNotFoundException("Section with id " + id + " doesn't exist"));

        return savedSection;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}/del")
    public void deleteSection(@PathVariable Long id) {
        SectionRepo.deleteById(id);
    }
}
