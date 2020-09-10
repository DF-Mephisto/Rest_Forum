package my.project.forum.service;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    /*boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;
*/
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        /*if (alreadySetup)
            return;

        Role adminRole = createRoleIfNotFound("ROLE_ADMIN", 0xFF0000);
        createRoleIfNotFound("ROLE_USER", 0x000000);

        createUserIfNotFound("admin", "admA1#4", "test@test.com", adminRole);

        alreadySetup = true;*/
    }

    /*User createUserIfNotFound(String name, String password, String email, Role role)
    {
        return userRepo.findByRole(role).orElseGet(() -> {
            User user = new User();
            user.setUsername(name);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setRole(role);
            userRepo.save(user);
            return user;
        });
    }

    Role createRoleIfNotFound(String name, Integer color) {

        return roleRepo.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            role.setColor(color);
            roleRepo.save(role);
            return role;
        });
    }*/

}
