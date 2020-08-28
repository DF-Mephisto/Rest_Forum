package my.project.forum.web;

import my.project.forum.dto.SectionDto;
import my.project.forum.entity.Section;
import my.project.forum.entity.Topic;
import my.project.forum.error.ActionNotAllowed;
import my.project.forum.error.ItemNotFoundException;
import my.project.forum.patch.SectionPatch;
import my.project.forum.repository.SectionRepository;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/sections")
public class SectionController {

    private SectionRepository sectionRepo;
    private TopicRepository topicRepo;
    private Properties props;

    @Autowired
    public SectionController(SectionRepository sectionRepo,
                             TopicRepository topicRepo,
                             Properties props)
    {
        this.sectionRepo = sectionRepo;
        this.topicRepo = topicRepo;
        this.props = props;
    }

    @GetMapping(produces = "application/json")
    public Page<Section> getSections(@RequestParam(value = "page", defaultValue = "0") int page)
    {
        Pageable pageable = PageRequest.of(page, props.getSectionsPageSize(),
                Sort.by(Sort.Direction.DESC, "placedAt"));

        return sectionRepo.findAll(pageable);
    }

    @PostMapping
    public ResponseEntity<Object> newSection(@Valid @RequestBody SectionDto sectionDto) {
        Section section = sectionDtoToSection(sectionDto);

        Section savedSection = sectionRepo.save(section);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedSection.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public Section getSections(@PathVariable Long id)
    {
        return sectionRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Section with id " + id + " doesn't exist"));
    }

    @PatchMapping("/{id}")
    public Section updateSection(@Valid @RequestBody SectionPatch patch, @PathVariable Long id) {

        Section patchedSection = sectionRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Section with id " + id + " doesn't exist"));

        if (patch.getName() != null)
        {
            if (patch.getName().isBlank())
                throw new ActionNotAllowed("Section name mustn't be blank");

            patchedSection.setName(patch.getName());
        }

        return sectionRepo.save(patchedSection);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteSection(@PathVariable Long id) {
        sectionRepo.deleteById(id);
    }

    @GetMapping("/{id}/topics")
    public Page<Topic> getSectionTopics(@PathVariable Long id,
                                   @RequestParam(value = "page", defaultValue = "0") int page)
    {
        if (sectionRepo.findById(id).isEmpty())
            throw new ItemNotFoundException("Section with id " + id + " doesn't exist");

        Pageable pageable = PageRequest.of(page, props.getTopicsPageSize());

        return topicRepo.findAllBySection_Id(id, pageable);
    }

    private Section sectionDtoToSection(SectionDto sectionDto)
    {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(sectionDto, Section.class);
    }
}
