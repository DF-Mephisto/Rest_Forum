package my.project.forum.security;

import my.project.forum.entity.User;
import my.project.forum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserRepositoryUserDetailsService implements UserDetailsService {

    private UserRepository UserRepo;

    @Autowired
    UserRepositoryUserDetailsService(UserRepository UserRepo)
    {
        this.UserRepo = UserRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User>  user = UserRepo.findByUsername(username);

        if (user.isPresent())
            return user.get();

        throw new UsernameNotFoundException("User " + username + " not found");
    }
}
