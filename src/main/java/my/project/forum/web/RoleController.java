package my.project.forum.web;

import my.project.forum.aop.annotation.Loggable;
import my.project.forum.data.postgres.entity.Role;
import my.project.forum.error.ActionNotAllowed;
import my.project.forum.error.ItemAlreadyExistsException;
import my.project.forum.error.ItemNotFoundException;
import my.project.forum.data.postgres.patch.RolePatch;
import my.project.forum.data.postgres.repository.RoleRepository;
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
    @Loggable(method = "get", controller = "role")
    public Iterable<Role> getAllRoles()
    {
        return roleRepo.findAll();
    }

    @PostMapping
    @Loggable(method = "post", controller = "role")
    public ResponseEntity<Object> newRole(@Valid @RequestBody Role role) {
        if (roleRepo.findByName(role.getName()).isPresent())
            throw new ItemAlreadyExistsException("Role with name " + role.getName() + " already exists");

        Role savedRole = roleRepo.save(role);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedRole.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    @Loggable(method = "get", controller = "role")
    public Role getRole(@PathVariable Long id)
    {
        return roleRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Role with id " + id + " doesn't exist"));
    }

    @PatchMapping("/{id}")
    @Loggable(method = "patch", controller = "role")
    public Role updateRole(@Valid @RequestBody RolePatch patch, @PathVariable Long id) {

        Role patchedRole = roleRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Role with id " + id + " doesn't exist"));

        if (patch.getName() != null)
        {
            if (patchedRole.getName().equals("ROLE_ADMIN"))
                throw new ActionNotAllowed("Admin role can't be renamed");

            if (patch.getName().isBlank())
                throw new ActionNotAllowed("Role name mustn't be blank");

            if (!patchedRole.getName().equals(patch.getName()) && roleRepo.findByName(patch.getName()).isPresent())
                throw new ItemAlreadyExistsException("Role with name " + patch.getName() + " already exists");

            patchedRole.setName(patch.getName());
        }

        if (patch.getColor() != null)
            patchedRole.setColor(patch.getColor());

        return roleRepo.save(patchedRole);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Loggable(method = "delete", controller = "role")
    public void deleteRole(@PathVariable Long id) {

        Role role = roleRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Role with id " + id + " doesn't exist"));

        if (role.getName().equals("ROLE_ADMIN"))
            throw new ActionNotAllowed("Admin role can't be removed");

        roleRepo.deleteById(id);
    }
}
