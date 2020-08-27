package my.project.forum.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.project.forum.builder.RoleBuilder;
import my.project.forum.builder.TagBuilder;
import my.project.forum.entity.Role;
import my.project.forum.entity.Tag;
import my.project.forum.repository.RoleRepository;
import my.project.forum.repository.TagRepository;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TagController.class)
public class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepositoryUserDetailsService userRepoService;

    @MockBean
    private TagRepository tagRepo;

    @BeforeEach
    public void setUp()
    {

    }

    //GET
    @Test
    public void findAll_TagsFound_ShouldReturnFoundTags() throws Exception
    {
        Tag tag1 = new TagBuilder().id(0L).name("Java").build();
        Tag tag2 = new TagBuilder().id(1L).name("C++").build();

        Mockito.when(tagRepo.findAll()).thenReturn(Arrays.asList(tag1, tag2));

        mockMvc.perform(get("/tag"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(0)))
                .andExpect(jsonPath("$[0].name", is("Java")))
                .andExpect(jsonPath("$[1].id", is(1)))
                .andExpect(jsonPath("$[1].name", is("C++")));

        verify(tagRepo, times(1)).findAll();
        verifyNoMoreInteractions(tagRepo);
    }

    @Test
    public void findById_TagNotFound_ShouldReturnHttpStatusCode404() throws Exception {
        mockMvc.perform(get("/tag/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(tagRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(tagRepo);
    }

    @Test
    public void findById_TagFound_ShouldReturnFoundTag() throws Exception {
        Tag found = new TagBuilder().id(1L).name("Java").build();

        when(tagRepo.findById(1L)).thenReturn(Optional.ofNullable(found));

        mockMvc.perform(get("/tag/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Java")));

        verify(tagRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(tagRepo);
    }

    //POST
    @Test
    @WithMockUser(roles = "ADMIN")
    public void add_TagWithInvalidFields_ShouldReturnValidationErrors() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        Tag tag = new TagBuilder().name(" ").build();

        mockMvc.perform(post("/tag")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(tag))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", containsInAnyOrder(
                        "Tag name can't be empty"
                )));

        verifyNoInteractions(tagRepo);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void add_TagWithExistingName_ShouldReturnValidationError() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Tag newTag = new TagBuilder().name("Java").build();
        Tag existingTag = new TagBuilder().id(1L).name("Java").build();

        when(tagRepo.findByName("Java")).thenReturn(Optional.ofNullable(existingTag));

        mockMvc.perform(post("/tag")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(newTag))
        )
                .andExpect(status().isConflict());

        verify(tagRepo, times(1)).findByName("Java");
        verifyNoMoreInteractions(tagRepo);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void add_Tag_ShouldAddTagAndReturnLocationHeader() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Tag tag = new TagBuilder().name("Java").build();
        Tag added = new TagBuilder().id(1L).name("Java").build();

        when(tagRepo.save(ArgumentMatchers.any(Tag.class))).thenReturn(added);

        mockMvc.perform(post("/tag")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(tag))
        )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/tag/1"));

        verify(tagRepo, times(1)).save(ArgumentMatchers.any(Tag.class));
    }

    //PATCH
    @Test
    @WithMockUser(roles = "ADMIN")
    public void patch_TagWithInvalidId_ShouldReturnHttpStatusCode404() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        Tag tag = new TagBuilder().name("Java").build();

        mockMvc.perform(patch("/tag/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(tag))
        )
                .andExpect(status().isNotFound());

        verify(tagRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(tagRepo);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void patch_TagWithInvalidFields_ShouldReturnValidationError() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        Tag newTag = new TagBuilder().name(" ").build();
        Tag patchedTag = new TagBuilder().id(1L).name("Java").build();

        when(tagRepo.findById(1L)).thenReturn(Optional.ofNullable(patchedTag));

        mockMvc.perform(patch("/tag/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(newTag))
        )
                .andExpect(status().isForbidden());

        verify(tagRepo, times(1)).findById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void patch_Tag_ShouldReturnPatchedTag() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        Tag newTag = new TagBuilder().name("C++").build();
        Tag patchedTag = new TagBuilder().id(1L).name("Java").build();
        Tag returnedTag = new TagBuilder().id(1L).name("C++").build();

        when(tagRepo.findById(1L)).thenReturn(Optional.ofNullable(patchedTag));
        when(tagRepo.save(ArgumentMatchers.any(Tag.class))).thenReturn(returnedTag);

        mockMvc.perform(patch("/tag/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(newTag))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("C++")));

        verify(tagRepo, times(1)).save(ArgumentMatchers.any(Tag.class));
    }

    //DELETE
    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_Tag_ShouldReturnNoContent() throws Exception {

    mockMvc.perform(delete("/tag/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(tagRepo, times(1)).deleteById(1L);
        verifyNoMoreInteractions(tagRepo);
    }
}