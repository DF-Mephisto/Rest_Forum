package my.project.forum.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.project.forum.builder.RoleBuilder;
import my.project.forum.entity.Role;
import my.project.forum.repository.RoleRepository;
import my.project.forum.security.UserRepositoryUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = RoleController.class)
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepositoryUserDetailsService userRepoService;

    @MockBean
    private RoleRepository roleRepo;

    @BeforeEach
    public void setUp()
    {

    }

    //GET
    @Test
    public void findAll_RolesFound_ShouldReturnFoundRoles() throws Exception
    {
        Role role1 = new RoleBuilder().id(0L).name("ROLE_ADMIN").color(0xFFFFFF).build();
        Role role2 = new RoleBuilder().id(1L).name("ROLE_USER").color(0x000000).build();

        Mockito.when(roleRepo.findAll()).thenReturn(Arrays.asList(role1, role2));

        mockMvc.perform(get("/role"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(0)))
                .andExpect(jsonPath("$[0].name", is("ROLE_ADMIN")))
                .andExpect(jsonPath("$[0].color", is(0xFFFFFF)))
                .andExpect(jsonPath("$[1].id", is(1)))
                .andExpect(jsonPath("$[1].name", is("ROLE_USER")))
                .andExpect(jsonPath("$[1].color", is(0x000000)));

        verify(roleRepo, times(1)).findAll();
        verifyNoMoreInteractions(roleRepo);
    }

    @Test
    public void findById_RoleNotFound_ShouldReturnHttpStatusCode404() throws Exception {
        mockMvc.perform(get("/role/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(roleRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(roleRepo);
    }

    @Test
    public void findById_RoleFound_ShouldReturnFoundRole() throws Exception {
        Role found = new RoleBuilder().id(1L).name("ROLE_ADMIN").color(0xFFFFFF).build();

        when(roleRepo.findById(1L)).thenReturn(Optional.ofNullable(found));

        mockMvc.perform(get("/role/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("ROLE_ADMIN")))
                .andExpect(jsonPath("$.color", is(0xFFFFFF)));

        verify(roleRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(roleRepo);
    }

    //POST
    @Test
    @WithMockUser(roles = "ADMIN")
    public void add_RoleWithInvalidFields_ShouldReturnValidationErrors() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        Role role = new RoleBuilder().name(" ").color(-1).build();

        mockMvc.perform(post("/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(role))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors", containsInAnyOrder(
                        "Role name can't be empty",
                               "Wrong color value"
                )));

        verifyNoInteractions(roleRepo);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void add_RoleWithExistingName_ShouldReturnValidationError() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Role newRole = new RoleBuilder().name("ROLE_ADMIN").color(0x00FF00).build();
        Role existingRole = new RoleBuilder().id(1L).name("ROLE_ADMIN").color(0xFFFFFF).build();

        when(roleRepo.findByName("ROLE_ADMIN")).thenReturn(Optional.ofNullable(existingRole));

        mockMvc.perform(post("/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(newRole))
        )
                .andExpect(status().isConflict());

        verify(roleRepo, times(1)).findByName("ROLE_ADMIN");
        verifyNoMoreInteractions(roleRepo);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void add_Role_ShouldAddRoleAndReturnLocationHeader() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Role role = new RoleBuilder().name("ROLE_ADMIN").color(0xFFFFFF).build();
        Role added = new RoleBuilder().id(1L).name("ROLE_ADMIN").color(0xFFFFFF).build();

        when(roleRepo.save(ArgumentMatchers.any(Role.class))).thenReturn(added);

        mockMvc.perform(post("/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(role))
        )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/role/1"));

        ArgumentCaptor<Role> dtoCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepo, times(1)).save(dtoCaptor.capture());
    }

    //PATCH
    @Test
    @WithMockUser(roles = "ADMIN")
    public void patch_RoleWithInvalidId_ShouldReturnHttpStatusCode404() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        Role role = new RoleBuilder().name("ROLE_ADMIN").color(0xFFFFFF).build();

        mockMvc.perform(patch("/role/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(role))
        )
                .andExpect(status().isNotFound());

        verify(roleRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(roleRepo);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void patch_RoleWithInvalidFields_ShouldReturnValidationError() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        Role newRole = new RoleBuilder().name("ROLE_USER").color(0x00FF00).build();
        Role patchedRole = new RoleBuilder().id(1L).name("ROLE_ADMIN").color(0xFFFFFF).build();

        when(roleRepo.findById(1L)).thenReturn(Optional.ofNullable(patchedRole));

        mockMvc.perform(patch("/role/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(newRole))
        )
                .andExpect(status().isForbidden());

        verify(roleRepo, times(1)).findById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void patch_Role_ShouldReturnPatchedRole() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        Role newRole = new RoleBuilder().name("ROLE_MODERATOR").color(0x00FF00).build();
        Role patchedRole = new RoleBuilder().id(1L).name("ROLE_USER").color(0xFFFFFF).build();
        Role returnedRole = new RoleBuilder().id(1L).name("ROLE_MODERATOR").color(0x00FF00).build();

        when(roleRepo.findById(1L)).thenReturn(Optional.ofNullable(patchedRole));
        when(roleRepo.save(ArgumentMatchers.any(Role.class))).thenReturn(returnedRole);

        mockMvc.perform(patch("/role/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(newRole))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("ROLE_MODERATOR")))
                .andExpect(jsonPath("$.color", is(0x00FF00)));

        verify(roleRepo, times(1)).save(ArgumentMatchers.any(Role.class));
    }

    //DELETE
    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_RoleWithInvalidId_ShouldReturnHttpStatusCode404() throws Exception {

        mockMvc.perform(delete("/role/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(roleRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(roleRepo);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_AdminRole_ShouldReturnForbidden() throws Exception {

        Role role = new RoleBuilder().id(1L).name("ROLE_ADMIN").color(0xFFFFFF).build();

        when(roleRepo.findById(1L)).thenReturn(Optional.ofNullable(role));

        mockMvc.perform(delete("/role/{id}", 1L))
                .andExpect(status().isForbidden());

        verify(roleRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(roleRepo);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_Role_ShouldReturnNoContent() throws Exception {

        Role role = new RoleBuilder().id(1L).name("ROLE_USER").color(0xFFFFFF).build();

        when(roleRepo.findById(1L)).thenReturn(Optional.ofNullable(role));

        mockMvc.perform(delete("/role/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(roleRepo, times(1)).findById(1L);
        verify(roleRepo, times(1)).deleteById(1L);
        verifyNoMoreInteractions(roleRepo);
    }
}
