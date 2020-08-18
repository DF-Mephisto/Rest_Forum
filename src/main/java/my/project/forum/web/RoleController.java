package my.project.forum.web;

import my.project.forum.entity.Role;
import my.project.forum.error.ItemAlreadyExistsException;
import my.project.forum.error.ItemNotFoundException;
import my.project.forum.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/role")
public class RoleController {

    private RoleRepository roleRepo;

    @Autowired
    public RoleController(RoleRepository roleRepo)
    {
        this.roleRepo = roleRepo;
    }

    @GetMapping(produces = "application/json")
    public Iterable<Role> getAllRoles()
    {
        return roleRepo.findAll();
    }

    @PostMapping
    public ResponseEntity<Object> newRole(@Valid @RequestBody Role role) {
        if (roleRepo.findByName(role.getName()).isPresent())
            throw new ItemAlreadyExistsException("Role with name " + role.getName() + " already exists");

        Role savedRole = roleRepo.save(role);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedRole.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public Role getRole(@PathVariable Long id)
    {
        return roleRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Role with id " + id + " doesn't exist"));
    }

    @PutMapping("/{id}")
    public Role updateRole(@Valid @RequestBody Role role, @PathVariable Long id) {
        if (roleRepo.findByNameExceptForId(role.getName(), id).isPresent())
            throw new ItemAlreadyExistsException("Role with name " + role.getName() + " already exists");

        Role savedRole = roleRepo.findById(id)
                .map(x -> {
                    x.setName(role.getName());
                    x.setColor(role.getColor());
                    return roleRepo.save(x);
                })
                .orElseThrow(() -> new ItemNotFoundException("Role with id " + id + " doesn't exist"));

        return savedRole;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable Long id) {
        roleRepo.deleteById(id);
    }
}
