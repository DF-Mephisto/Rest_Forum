package my.project.forum.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.project.forum.builder.dto.SectionDtoBuilder;
import my.project.forum.builder.dto.TopicDtoBuilder;
import my.project.forum.builder.entity.CommentBuilder;
import my.project.forum.builder.entity.SectionBuilder;
import my.project.forum.builder.entity.TagBuilder;
import my.project.forum.builder.entity.TopicBuilder;
import my.project.forum.dto.SectionDto;
import my.project.forum.dto.TopicDto;
import my.project.forum.entity.Comment;
import my.project.forum.entity.Role;
import my.project.forum.entity.Section;
import my.project.forum.entity.Topic;
import my.project.forum.patch.SectionPatch;
import my.project.forum.patch.TopicPatch;
import my.project.forum.repository.CommentRepository;
import my.project.forum.repository.SectionRepository;
import my.project.forum.repository.TopicRepository;
import my.project.forum.security.UserRepositoryUserDetailsService;
import my.project.forum.service.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TopicController.class)
public class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepositoryUserDetailsService userRepoService;

    @MockBean
    private TopicRepository topicRepo;

    @MockBean
    private CommentRepository commentRepo;

    @MockBean
    private Properties props;

    @BeforeEach
    public void setUp()
    {
        Mockito.when(props.getTopicsPageSize()).thenReturn(2);
    }

    //GET
    @Test
    public void findAll_TopicsFound_ShouldReturnFoundTopics() throws Exception
    {
        Topic topic1 = new TopicBuilder().id(0L).name("Spring Framework").build();
        Topic topic2 = new TopicBuilder().id(1L).name("Hibernate").build();

        Mockito.when(topicRepo.findAll(ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(topic1, topic2)));

        mockMvc.perform(get("/topics")
                .param("page", "0")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(0)))
                .andExpect(jsonPath("$.content[0].name", is("Spring Framework")))
                .andExpect(jsonPath("$.content[1].id", is(1)))
                .andExpect(jsonPath("$.content[1].name", is("Hibernate")));

        verify(topicRepo, times(1)).findAll(ArgumentMatchers.any(Pageable.class));
        verifyNoMoreInteractions(topicRepo);
    }

    @Test
    public void findById_TopicNotFound_ShouldReturnHttpStatusCode404() throws Exception {
        mockMvc.perform(get("/topics/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(topicRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(topicRepo);
    }

    @Test
    public void findById_TopicFound_ShouldReturnFoundTopic() throws Exception {
        Topic found = new TopicBuilder().id(1L).name("Spring Framework").views(1L).build();

        when(topicRepo.findById(1L)).thenReturn(Optional.ofNullable(found));

        mockMvc.perform(get("/topics/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Spring Framework")));

        verify(topicRepo, times(1)).findById(1L);

        ArgumentCaptor<Topic> dtoCaptor = ArgumentCaptor.forClass(Topic.class);
        verify(topicRepo, times(1)).save(dtoCaptor.capture());

        Topic updated = dtoCaptor.getValue();
        assertThat(updated.getViews()).isEqualTo(2L);

        verifyNoMoreInteractions(topicRepo);
    }

    //POST
    @Test
    @WithMockUser(roles = "USER")
    public void add_TopicWithInvalidFields_ShouldReturnValidationErrors() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        TopicDto topicDto = new TopicDtoBuilder().name(" ").section(null).build();

        mockMvc.perform(post("/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(topicDto))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors", containsInAnyOrder(
                        "Topic name can't be empty",
                        "Parent section can't be null"
                )));

        verifyNoInteractions(topicRepo);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void add_Topic_ShouldAddTopicAndReturnLocationHeader() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        TopicDto topicDto = new TopicDtoBuilder().name("Spring Framework").build();
        Topic added = new TopicBuilder().id(1L).name("Spring Framework").build();

        when(topicRepo.save(ArgumentMatchers.any(Topic.class))).thenReturn(added);

        mockMvc.perform(post("/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(topicDto))
        )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/topics/1"));

        verify(topicRepo, times(1)).save(ArgumentMatchers.any(Topic.class));
    }

    //PATCH
    @Test
    @WithMockUser(roles = "ADMIN")
    public void patch_TopicWithInvalidId_ShouldReturnHttpStatusCode404() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        TopicPatch patch = new TopicPatch("Programming", Set.of(new TagBuilder().build()));

        mockMvc.perform(patch("/topics/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patch))
        )
                .andExpect(status().isNotFound());

        verify(topicRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(topicRepo);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void patch_TopicWithInvalidFields_ShouldReturnValidationError() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        TopicPatch patch = new TopicPatch(" ", null);
        Topic patchedTopic = new TopicBuilder().build();

        when(topicRepo.findById(1L)).thenReturn(Optional.ofNullable(patchedTopic));

        mockMvc.perform(patch("/topics/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patch))
        )
                .andExpect(status().isForbidden());

        verify(topicRepo, times(1)).findById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void patch_Topic_ShouldReturnPatchedTopic() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        TopicPatch patch = new TopicPatch("Spring Framework", Set.of(new TagBuilder().name("Java").build()));
        Topic patchedTopic = new TopicBuilder().id(1L).name("Hibernate").build();
        Topic returnedTopic = new TopicBuilder().id(1L).name("Spring Framework")
                .tags(Set.of(new TagBuilder().name("Java").build())).build();

        when(topicRepo.findById(1L)).thenReturn(Optional.ofNullable(patchedTopic));
        when(topicRepo.save(ArgumentMatchers.any(Topic.class))).thenReturn(returnedTopic);

        mockMvc.perform(patch("/topics/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patch))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Spring Framework")))
                .andExpect(jsonPath("$.tags[0].name", is("Java")));

        verify(topicRepo, times(1)).save(ArgumentMatchers.any(Topic.class));
    }

    //DELETE
    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_Topic_ShouldReturnNoContent() throws Exception {

        mockMvc.perform(delete("/topics/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(topicRepo, times(1)).deleteById(1L);
        verifyNoMoreInteractions(topicRepo);
    }

    //GET COMMENTS
    @Test
    public void findCommentsByTopicId_TopicNotFound_ShouldReturnHttpStatusCode404() throws Exception {
        mockMvc.perform(get("/topics/{id}/comments", 1L))
                .andExpect(status().isNotFound());

        verify(topicRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(topicRepo);
    }

    @Test
    public void findCommentsByTopicId_Topic_ShouldReturnFoundComments() throws Exception {

        Topic found = new TopicBuilder().id(1L).build();

        Comment comment1 = new CommentBuilder().id(0L).text("Hello!").build();
        Comment comment2 = new CommentBuilder().id(1L).text("Welcome!").build();

        when(topicRepo.findById(1L)).thenReturn(Optional.ofNullable(found));
        Mockito.when(commentRepo.findAllByTopic_Id(eq(1L), any()))
                .thenReturn(new PageImpl<>(Arrays.asList(comment1, comment2)));
        Mockito.when(props.getCommentsPageSize()).thenReturn(5);

        mockMvc.perform(get("/topics/{id}/comments", 1L)
                .param("page", "0")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(0)))
                .andExpect(jsonPath("$.content[0].text", is("Hello!")))
                .andExpect(jsonPath("$.content[1].id", is(1)))
                .andExpect(jsonPath("$.content[1].text", is("Welcome!")));

        verify(topicRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(topicRepo);

        verify(commentRepo, times(1)).findAllByTopic_Id(eq(1L), any());
        verifyNoMoreInteractions(commentRepo);
    }
}
