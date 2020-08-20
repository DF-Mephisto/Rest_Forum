package my.project.forum.web;

import my.project.forum.entity.Role;
import my.project.forum.entity.Tag;
import my.project.forum.entity.User;
import my.project.forum.error.ItemAlreadyExistsException;
import my.project.forum.error.ItemNotFoundException;
import my.project.forum.repository.RoleRepository;
import my.project.forum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserRepository userRepo;
    private RoleRepository roleRepo;
    private PasswordEncoder encoder;

    @Autowired
    public UserController(UserRepository userRepo,
                          RoleRepository roleRepo,
                          PasswordEncoder encoder)
    {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
    }

    @GetMapping(produces = "application/json")
    public Iterable<User> getAllUsers()
    {
        return userRepo.findAll();
    }

    @PostMapping
    public ResponseEntity<Object> newUser(@Valid @RequestBody User user) {
        if (userRepo.findByUsername(user.getUsername()).isPresent())
            throw new ItemAlreadyExistsException("User with name " + user.getUsername() + " already exists");

        user.setPassword(encoder.encode(user.getPassword()));
        User savedUser = userRepo.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedUser.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id)
    {
        return userRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("User with id " + id + " doesn't exist"));
    }

    @PutMapping("/{id}")
    public User updateUser(@Valid @RequestBody User user, @PathVariable Long id) {

        Role role = roleRepo.findById(user.getRole().getId())
                .orElseThrow(() -> new ItemNotFoundException("Role with id " + user.getRole().getId() + " doesn't exist"));

        User savedUser = userRepo.findById(id)
                .map(x -> {
                    x.setUsername(user.getUsername());
                    x.setPassword(encoder.encode(user.getPassword()));
                    x.setAvatar(user.getAvatar());
                    x.setEmail(user.getEmail());
                    x.setInformation(user.getInformation());
                    x.setRole(role);
                    return userRepo.save(x);
                })
                .orElseThrow(() -> new ItemNotFoundException("User with id " + id + " doesn't exist"));

        return savedUser;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepo.deleteById(id);
    }

    @PostMapping("/{id}/lock")
    public void lockUser(@PathVariable Long id)
    {
        userRepo.findById(id)
                .map(x -> {
                    x.setNonLocked(!x.isNonLocked());
                    return userRepo.save(x);
                })
                .orElseThrow(() -> new ItemNotFoundException("User with id " + id + " doesn't exist"));
    }
}
