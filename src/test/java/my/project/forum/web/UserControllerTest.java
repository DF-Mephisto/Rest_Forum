package my.project.forum.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.project.forum.data.builder.dto.UserDtoBuilder;
import my.project.forum.data.builder.entity.RoleBuilder;
import my.project.forum.data.builder.entity.SectionBuilder;
import my.project.forum.data.builder.entity.TopicBuilder;
import my.project.forum.data.builder.entity.UserBuilder;
import my.project.forum.data.postgres.dto.UserDto;
import my.project.forum.data.postgres.entity.*;
import my.project.forum.data.postgres.repository.ReputationRepository;
import my.project.forum.error.CustomGlobalExceptionHandler;
import my.project.forum.data.postgres.patch.UserProfilePatch;
import my.project.forum.data.postgres.repository.RoleRepository;
import my.project.forum.data.postgres.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
public class UserControllerTest {

    //@Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepo;

    @MockBean
    private RoleRepository roleRepo;

    @MockBean
    private ReputationRepository repRepo;

    @MockBean
    private PasswordEncoder encoder;

    @BeforeEach
    public void setUp()
    {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new UserController(userRepo, roleRepo, repRepo, encoder))
                .setControllerAdvice(new CustomGlobalExceptionHandler())
                .setCustomArgumentResolvers(putAuthenticationPrincipal)
                .build();
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

        UserDto userDto = new UserDtoBuilder().username(" ")
                .password("123").email("email")
                .build();

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(userDto))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors", hasSize(4)))
                .andExpect(jsonPath("$.errors", containsInAnyOrder(
                        "Username can't be empty",
                               "Name must be between 4 and 20 in length",
                               "Password must be minimum 8 characters in length and include at least one digit, lower-case, " +
                                    "upper-case and special characters and mustn't include any space symbols",
                               "Wrong email"
                )));

        verifyNoInteractions(userRepo);
    }

    @Test
    public void add_UserWithExistingName_ShouldReturnValidationError() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        UserDto userDto = new UserDtoBuilder().username("test user")
                .build();

        User existingUser = new UserBuilder().username("test user").build();

        when(userRepo.findByUsername("test user")).thenReturn(Optional.ofNullable(existingUser));

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(userDto))
        )
                .andExpect(status().isConflict());

        verify(userRepo, times(1)).findByUsername("test user");
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    public void add_User_ShouldAddUserAndReturnLocationHeader() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        UserDto userDto = new UserDtoBuilder().username("test user")
                .password("tT#11111").email("email@test.ru")
                .build();

        User added = new UserBuilder().id(1L).username("test user")
                .password("tT#11111").email("email@test.ru")
                .build();

        when(userRepo.save(ArgumentMatchers.any(User.class))).thenReturn(added);

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(userDto))
        )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/user/1"));

        verify(userRepo, times(1)).save(ArgumentMatchers.any(User.class));
    }

    //PATCH
    @Test
    public void patch_UserWithNoAccess_ShouldReturnForbidden() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        UserProfilePatch patch = new UserProfilePatch("new user", "new info", "newmail@mail.ru",
                "tT#12111", "tT1#1111", null,
                new RoleBuilder().name("ROLE_MODERATOR").build());

        mockMvc.perform(patch("/user/{id}", 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patch))
        )
                .andExpect(status().isForbidden());

        verifyNoInteractions(userRepo);
    }

    @Test
    public void patch_UserWithInvalidFields_ShouldReturnValidationError() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        UserProfilePatch patch = new UserProfilePatch("u", "new info", "mail",
                "p", "tT1#1111", null,
                new RoleBuilder().name("ROLE_MODERATOR").build());

        mockMvc.perform(patch("/user/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patch))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors", hasSize(3)))
                .andExpect(jsonPath("$.errors", containsInAnyOrder(
                        "Name must be between 4 and 20 in length",
                        "Password must be minimum 8 characters in length and include at least one digit, lower-case, " +
                                "upper-case and special characters and mustn't include any space symbols",
                        "Wrong email"
                )));

        verifyNoInteractions(userRepo);
    }

    @Test
    public void patch_UserWithWrongOldPassword_ShouldReturnForbidden1() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        UserProfilePatch patch = new UserProfilePatch("new user", "new info", "newmail@mail.ru",
                "tT#12111", "tT1#1111", null,
                new RoleBuilder().name("ROLE_MODERATOR").build());

        User patchedUser = new UserBuilder().id(1L).username("user").information("info")
                .email("mail@mail.ru").password("tT3#1111").avatar(null)
                .role(new RoleBuilder().id(1L).name("ROLE_USER").build()).build();

        when(userRepo.findById(1L)).thenReturn(Optional.ofNullable(patchedUser));
        when(encoder.matches(any(), any())).thenReturn(false);

        mockMvc.perform(patch("/user/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patch))
        )
                .andExpect(status().isForbidden());

        verify(userRepo, times(1)).findById(1L);
    }

    @Test
    public void patch_User_ShouldReturnPatchedUser() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        UserProfilePatch patch = new UserProfilePatch("new user", "new info", "newmail@mail.ru",
                "tT#12111", "tT1#1111", null,
                new RoleBuilder().name("ROLE_MODERATOR").build());

        User patchedUser = new UserBuilder().id(1L).username("user").information("info")
                .email("mail@mail.ru").password("tT1#1111").avatar(null)
                .role(new Role(1L, "ROLE_USER", 0xFF00FF)).build();

        User returnedUser = new UserBuilder().id(1L).username("user").information("new info")
                .email("newmail@mail.ru").password("tT2#1111").avatar(null)
                .role(new Role(1L, "ROLE_USER", 0xFF00FF)).build();

        when(userRepo.findById(1L)).thenReturn(Optional.ofNullable(patchedUser));
        when(userRepo.save(ArgumentMatchers.any(User.class))).thenReturn(returnedUser);
        when(encoder.matches(any(), any())).thenReturn(true);

        mockMvc.perform(patch("/user/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patch))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("user")))
                .andExpect(jsonPath("$.email", is("newmail@mail.ru")))
                .andExpect(jsonPath("$.information", is("new info")))
                .andExpect(jsonPath("$.role.name", is("ROLE_USER")));

        verify(userRepo, times(1)).save(ArgumentMatchers.any(User.class));
    }

    //DELETE
    @Test
    public void delete_UserWithNoAccess_ShouldReturnForbidden() throws Exception {

        mockMvc.perform(delete("/user/{id}", 2L))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userRepo);
    }

    @Test
    public void delete_User_ShouldReturnNoContent() throws Exception {

        mockMvc.perform(delete("/user/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(userRepo, times(1)).deleteById(1L);
        verifyNoMoreInteractions(userRepo);
    }

    //POST LOCK
    @Test
    public void postLock_UserWithInvalidId_ShouldReturnHttpStatusCode404() throws Exception {

        mockMvc.perform(post("/user/{id}/lock", 1L))
                .andExpect(status().isNotFound());

        verify(userRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    public void postLock_User_ShouldReturnLockedUser() throws Exception {

        User foundUser = new UserBuilder().nonLocked(true).build();
        User returnedUser = new UserBuilder().nonLocked(false).build();

        when(userRepo.findById(1L)).thenReturn(Optional.ofNullable(foundUser));
        when(userRepo.save(ArgumentMatchers.any(User.class))).thenReturn(returnedUser);

        mockMvc.perform(post("/user/{id}/lock", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nonLocked", is(false)));

        verify(userRepo, times(1)).findById(1L);
        verify(userRepo, times(1)).save(ArgumentMatchers.any(User.class));
        verifyNoMoreInteractions(userRepo);
    }

    //GET REPUTATIONS
    @Test
    public void findReputationsByTargetUserId_UserNotFound_ShouldReturnHttpStatusCode404() throws Exception {
        mockMvc.perform(get("/user/{id}/reputation", 1L))
                .andExpect(status().isNotFound());

        verify(userRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    public void findReputationsByTargetUserId_User_ShouldReturnFoundReputations() throws Exception {

        User found = new UserBuilder().id(1L).build();

        Reputation rep1 = Reputation.builder().id(0L).msg("thanks1").target(found).build();
        Reputation rep2 = Reputation.builder().id(1L).msg("thanks2").target(found).build();

        when(userRepo.findById(1L)).thenReturn(Optional.ofNullable(found));
        Mockito.when(repRepo.findAllByTargetId(eq(1L)))
                .thenReturn(Arrays.asList(rep1, rep2));

        mockMvc.perform(get("/user/{id}/reputation", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(0)))
                .andExpect(jsonPath("$[0].msg", is("thanks1")))
                .andExpect(jsonPath("$[1].id", is(1)))
                .andExpect(jsonPath("$[1].msg", is("thanks2")));

        verify(userRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(userRepo);

        verify(repRepo, times(1)).findAllByTargetId(eq(1L));
        verifyNoMoreInteractions(repRepo);
    }

    private HandlerMethodArgumentResolver putAuthenticationPrincipal = new HandlerMethodArgumentResolver() {
        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.getParameterType().isAssignableFrom(User.class);
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
            return new UserBuilder().id(1L).username("user").information("info")
                    .email("mail@mail.ru").password("tT1#1111").avatar(null)
                    .role(new Role(1L, "ROLE_USER", 0xFF00FF)).build();
        }
    };
}
