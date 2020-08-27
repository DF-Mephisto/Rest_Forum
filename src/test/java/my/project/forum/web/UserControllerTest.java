package my.project.forum.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.project.forum.builder.RoleBuilder;
import my.project.forum.builder.UserBuilder;
import my.project.forum.entity.Role;
import my.project.forum.entity.User;
import my.project.forum.repository.RoleRepository;
import my.project.forum.repository.UserRepository;
import my.project.forum.security.UserRepositoryUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepositoryUserDetailsService userRepoService;

    @MockBean
    private UserRepository userRepo;

    @MockBean
    private RoleRepository roleRepo;

    @Autowired
    private PasswordEncoder encoder;

    @BeforeEach
    public void setUp()
    {

    }

    //GET
    @Test
    public void findAll_UsersFound_ShouldReturnFoundUsers() throws Exception
    {
        User user1 = new UserBuilder().id(0L).username("user 1")
                .role(new RoleBuilder().id(0L).name("ROLE_ADMIN").build())
                .build();

        User user2 = new UserBuilder().id(1L).username("user 2")
                .role(new RoleBuilder().id(1L).name("ROLE_USER").build())
                .build();

        Mockito.when(userRepo.findAll()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(0)))
                .andExpect(jsonPath("$[0].username", is("user 1")))
                .andExpect(jsonPath("$[0].role.name", is("ROLE_ADMIN")))
                .andExpect(jsonPath("$[1].id", is(1)))
                .andExpect(jsonPath("$[1].username", is("user 2")))
                .andExpect(jsonPath("$[1].role.name", is("ROLE_USER")));

        verify(userRepo, times(1)).findAll();
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    public void findById_UserNotFound_ShouldReturnHttpStatusCode404() throws Exception {
        mockMvc.perform(get("/user/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(userRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    public void findById_UserFound_ShouldReturnFoundUser() throws Exception {
        User found = new UserBuilder().id(0L).username("user 1")
                .role(new RoleBuilder().id(0L).name("ROLE_ADMIN").build())
                .build();

        when(userRepo.findById(0L)).thenReturn(Optional.ofNullable(found));

        mockMvc.perform(get("/user/{id}", 0L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(0)))
                .andExpect(jsonPath("$.username", is("user 1")))
                .andExpect(jsonPath("$.role.name", is("ROLE_ADMIN")));

        verify(userRepo, times(1)).findById(0L);
        verifyNoMoreInteractions(userRepo);
    }

    //POST
    @Test
    public void add_UserWithInvalidFields_ShouldReturnValidationErrors() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        User user = new UserBuilder().username(" ")
                .password("123").email("email")
                .build();

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(user))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors", hasSize(3)))
                .andExpect(jsonPath("$.errors", containsInAnyOrder(
                        "Username can't be empty",
                               "Password must be minimum 8 characters in length and include at least one digit, lower-case, " +
                                    "upper-case and special characters and mustn't include any space symbols",
                               "Email can't be null"
                )));

        verifyNoInteractions(userRepo);
    }
}
