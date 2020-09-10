package my.project.forum.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.project.forum.data.builder.dto.SectionDtoBuilder;
import my.project.forum.data.builder.entity.SectionBuilder;
import my.project.forum.data.builder.entity.TopicBuilder;
import my.project.forum.data.postgres.dto.SectionDto;
import my.project.forum.data.postgres.entity.Section;
import my.project.forum.data.postgres.entity.Topic;
import my.project.forum.data.postgres.patch.SectionPatch;
import my.project.forum.data.postgres.repository.SectionRepository;
import my.project.forum.data.postgres.repository.TopicRepository;
import my.project.forum.security.UserRepositoryUserDetailsService;
import my.project.forum.service.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = SectionController.class)
public class SectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepositoryUserDetailsService userRepoService;

    @MockBean
    private SectionRepository sectionRepo;

    @MockBean
    private TopicRepository topicRepo;

    @MockBean
    private Properties props;

    @BeforeEach
    public void setUp()
    {
        Mockito.when(props.getSectionsPageSize()).thenReturn(2);
    }

    //GET
    @Test
    public void findAll_SectionsFound_ShouldReturnFoundSections() throws Exception
    {
        Section section1 = new SectionBuilder().id(0L).name("Programming").build();
        Section section2 = new SectionBuilder().id(1L).name("Discussions").build();

        Mockito.when(sectionRepo.findAll(ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(section1, section2)));

        mockMvc.perform(get("/sections")
                .param("page", "0")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(0)))
                .andExpect(jsonPath("$.content[0].name", is("Programming")))
                .andExpect(jsonPath("$.content[1].id", is(1)))
                .andExpect(jsonPath("$.content[1].name", is("Discussions")));

        verify(sectionRepo, times(1)).findAll(ArgumentMatchers.any(Pageable.class));
        verifyNoMoreInteractions(sectionRepo);
    }

    @Test
    public void findById_SectionNotFound_ShouldReturnHttpStatusCode404() throws Exception {
        mockMvc.perform(get("/sections/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(sectionRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(sectionRepo);
    }

    @Test
    public void findById_SectionFound_ShouldReturnFoundSection() throws Exception {
        Section found = new SectionBuilder().id(1L).name("Programming").build();

        when(sectionRepo.findById(1L)).thenReturn(Optional.ofNullable(found));

        mockMvc.perform(get("/sections/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Programming")));

        verify(sectionRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(sectionRepo);
    }

    //POST
    @Test
    @WithMockUser(roles = "ADMIN")
    public void add_SectionWithInvalidFields_ShouldReturnValidationErrors() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        SectionDto sectionDto = new SectionDtoBuilder().name(" ").build();

        mockMvc.perform(post("/sections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(sectionDto))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", containsInAnyOrder(
                        "Section name can't be empty"
                )));

        verifyNoInteractions(sectionRepo);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void add_Section_ShouldAddSectionAndReturnLocationHeader() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        SectionDto sectionDto = new SectionDtoBuilder().name("Programming").build();
        Section added = new SectionBuilder().id(1L).name("Programming").build();

        when(sectionRepo.save(ArgumentMatchers.any(Section.class))).thenReturn(added);

        mockMvc.perform(post("/sections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(sectionDto))
        )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/sections/1"));

        verify(sectionRepo, times(1)).save(ArgumentMatchers.any(Section.class));
    }

    //PATCH
    @Test
    @WithMockUser(roles = "ADMIN")
    public void patch_SectionWithInvalidId_ShouldReturnHttpStatusCode404() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        SectionPatch patch = new SectionPatch("Programming");

        mockMvc.perform(patch("/sections/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patch))
        )
                .andExpect(status().isNotFound());

        verify(sectionRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(sectionRepo);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void patch_SectionWithInvalidFields_ShouldReturnValidationError() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        SectionPatch patch = new SectionPatch(" ");
        Section patchedSection = new SectionBuilder().build();

        when(sectionRepo.findById(0L)).thenReturn(Optional.ofNullable(patchedSection));

        mockMvc.perform(patch("/sections/{id}", 0L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patch))
        )
                .andExpect(status().isForbidden());

        verify(sectionRepo, times(1)).findById(0L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void patch_Section_ShouldReturnPatchedSection() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        SectionPatch patch = new SectionPatch("Programming");
        Section patchedSection = new SectionBuilder().id(1L).name("Discussions").build();
        Section returnedSection = new SectionBuilder().id(1L).name("Programming").build();

        when(sectionRepo.findById(1L)).thenReturn(Optional.ofNullable(patchedSection));
        when(sectionRepo.save(ArgumentMatchers.any(Section.class))).thenReturn(returnedSection);

        mockMvc.perform(patch("/sections/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patch))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Programming")));

        verify(sectionRepo, times(1)).save(ArgumentMatchers.any(Section.class));
    }

    //DELETE
    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_Section_ShouldReturnNoContent() throws Exception {

        mockMvc.perform(delete("/sections/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(sectionRepo, times(1)).deleteById(1L);
        verifyNoMoreInteractions(sectionRepo);
    }

    //GET TOPICS
    @Test
    public void findTopicsBySectionId_SectionNotFound_ShouldReturnHttpStatusCode404() throws Exception {
        mockMvc.perform(get("/sections/{id}/topics", 1L))
                .andExpect(status().isNotFound());

        verify(sectionRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(sectionRepo);
    }

    @Test
    public void findTopicsBySectionId_Section_ShouldReturnFoundTopics() throws Exception {

        Section found = new SectionBuilder().id(1L).build();

        Topic topic1 = new TopicBuilder().id(0L).name("Spring Framework").build();
        Topic topic2 = new TopicBuilder().id(1L).name("Hibernate").build();

        when(sectionRepo.findById(1L)).thenReturn(Optional.ofNullable(found));
        Mockito.when(topicRepo.findAllBySection_Id(eq(1L), any()))
                .thenReturn(new PageImpl<>(Arrays.asList(topic1, topic2)));
        Mockito.when(props.getTopicsPageSize()).thenReturn(2);

        mockMvc.perform(get("/sections/{id}/topics", 1L)
                .param("page", "0")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(0)))
                .andExpect(jsonPath("$.content[0].name", is("Spring Framework")))
                .andExpect(jsonPath("$.content[1].id", is(1)))
                .andExpect(jsonPath("$.content[1].name", is("Hibernate")));

        verify(sectionRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(sectionRepo);

        verify(topicRepo, times(1)).findAllBySection_Id(eq(1L), any());
        verifyNoMoreInteractions(topicRepo);
    }

}
