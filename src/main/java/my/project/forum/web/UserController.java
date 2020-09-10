package my.project.forum.web;

import my.project.forum.aop.annotation.Loggable;
import my.project.forum.data.postgres.dto.UserDto;
import my.project.forum.data.postgres.entity.User;
import my.project.forum.error.ActionNotAllowed;
import my.project.forum.error.ItemAlreadyExistsException;
import my.project.forum.error.ItemNotFoundException;
import my.project.forum.data.postgres.patch.UserProfilePatch;
import my.project.forum.data.postgres.repository.RoleRepository;
import my.project.forum.data.postgres.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
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
    @Loggable(method = "get", controller = "user")
    public Iterable<User> getAllUsers()
    {
        return userRepo.findAll();
    }

    @PostMapping
    @Loggable(method = "post", controller = "user")
    public ResponseEntity<Object> newUser(@Valid @RequestBody UserDto userDto) {
        User user = userDtoToUser(userDto);

        if (userRepo.findByUsername(user.getUsername()).isPresent())
            throw new ItemAlreadyExistsException("User with name " + user.getUsername() + " already exists");

        user.setPassword(encoder.encode(user.getPassword()));
        User savedUser = userRepo.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedUser.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    @Loggable(method = "get", controller = "user")
    public User getUser(@PathVariable Long id)
    {
        return userRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("User with id " + id + " doesn't exist"));
    }

    @PatchMapping("/{id}")
    @Loggable(method = "patch", controller = "user")
    public User updateUser(@Valid @RequestBody UserProfilePatch patch,
                           @PathVariable Long id,
                           @AuthenticationPrincipal User user) {

        boolean hasAdminRole = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean sameUser = user.getId().equals(id);

        if (!sameUser && !hasAdminRole)
            throw new ActionNotAllowed("Access denied");

        User patchedUser = userRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("User with id " + id + " doesn't exist"));

        //Admin only patches
        if (hasAdminRole)
        {
            if (patch.getRole() != null)
                patchedUser.setRole(roleRepo.findById(patch.getRole().getId())
                        .orElseThrow(() -> new ItemNotFoundException("Role with id " + patch.getRole().getId() + " doesn't exist")));

            if (patch.getUsername() != null)
            {
                if (!patchedUser.getUsername().equals(patch.getUsername()) && userRepo.findByUsername(patch.getUsername()).isPresent())
                    throw new ItemAlreadyExistsException("User with name " + patch.getUsername() + " already exists");

                patchedUser.setUsername(patch.getUsername());
            }
        }

        //User only patches
        if (sameUser)
        {
            if (patch.getEmail() != null)
                patchedUser.setEmail(patch.getEmail());

            if (patch.getPassword() != null)
            {
                if (patch.getOldPassword() == null || !encoder.matches(patch.getOldPassword(), user.getPassword()))
                    throw new ActionNotAllowed("Old password doesn't match current password");

                patchedUser.setPassword(encoder.encode(patch.getPassword()));
            }
        }

        if (patch.getInformation() != null)
            patchedUser.setInformation(patch.getInformation());

        if (patch.getAvatar() != null)
            patchedUser.setAvatar(patch.getAvatar());

        userRepo.save(patchedUser);

        if (sameUser)
        {
            Authentication authentication = new UsernamePasswordAuthenticationToken(patchedUser, patchedUser.getPassword(), patchedUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        return patchedUser;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Loggable(method = "delete", controller = "user")
    public void deleteUser(@PathVariable Long id,
                           @AuthenticationPrincipal User user,
                           HttpServletRequest request) throws ServletException {

        if (!user.getId().equals(id) && !user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")))
            throw new ActionNotAllowed("Access denied");

        if (user.getId().equals(id))
            request.logout();

        userRepo.deleteById(id);
    }

    @PostMapping("/{id}/lock")
    @Loggable(method = "post", controller = "user")
    public User lockUser(@PathVariable Long id)
    {
        return userRepo.findById(id)
                .map(x -> {
                    x.setNonLocked(!x.isNonLocked());
                    return userRepo.save(x);
                })
                .orElseThrow(() -> new ItemNotFoundException("User with id " + id + " doesn't exist"));
    }

    private User userDtoToUser(UserDto userDto)
    {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(userDto, User.class);
    }
}
