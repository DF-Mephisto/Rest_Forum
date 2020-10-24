package my.project.forum.web;

import my.project.forum.aop.annotation.Loggable;
import my.project.forum.data.postgres.dto.LikeDto;
import my.project.forum.data.postgres.dto.ReputationDto;
import my.project.forum.data.postgres.entity.Comment;
import my.project.forum.data.postgres.entity.Like;
import my.project.forum.data.postgres.entity.Reputation;
import my.project.forum.data.postgres.entity.User;
import my.project.forum.data.postgres.repository.ReputationRepository;
import my.project.forum.error.ActionNotAllowed;
import my.project.forum.error.ItemNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/reputations")
public class ReputationController {

    private ReputationRepository repRepo;

    @Autowired
    public ReputationController(ReputationRepository repRepo)
    {
        this.repRepo = repRepo;
    }

    @GetMapping(produces = "application/json")
    @Loggable(method = "get", controller = "reputation")
    public Iterable<Reputation> getReputations()
    {
        return repRepo.findAll();
    }

    @PostMapping
    @Loggable(method = "post", controller = "reputation")
    public ResponseEntity<Reputation> newReputation(@Valid @RequestBody ReputationDto repDto,
                                                           @AuthenticationPrincipal User user) {
        Reputation rep = repDtoToRep(repDto);

        if (rep.getTarget().getId().equals(user.getId()))
            throw new ActionNotAllowed("You can't increase your own reputation");

        rep.setUser(user);

        Reputation savedReputation = repRepo.save(rep);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedReputation.getId()).toUri();

        return ResponseEntity.created(location).body(savedReputation);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Loggable(method = "delete", controller = "reputation")
    public void deleteRep(@PathVariable("id") Long id) {
        repRepo.deleteById(id);
    }

    private Reputation repDtoToRep(ReputationDto repDto)
    {
        ModelMapper modelMapper = new ModelMapper();

        return modelMapper.map(repDto, Reputation.class);
    }

}
