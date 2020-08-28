package my.project.forum.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.project.forum.builder.dto.CommentDtoBuilder;
import my.project.forum.builder.dto.TopicDtoBuilder;
import my.project.forum.builder.entity.*;
import my.project.forum.dto.CommentDto;
import my.project.forum.entity.*;
import my.project.forum.error.CustomGlobalExceptionHandler;
import my.project.forum.patch.CommentPatch;
import my.project.forum.repository.CommentRepository;
import my.project.forum.repository.LikeRepository;
import my.project.forum.service.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
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
public class CommentControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private CommentRepository commentRepo;

    @MockBean
    private LikeRepository likeRepo;

    @MockBean
    private Properties props;

    @BeforeEach
    public void setUp()
    {
        Mockito.when(props.getCommentsPageSize()).thenReturn(5);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new CommentController(commentRepo, likeRepo, props))
                .setControllerAdvice(new CustomGlobalExceptionHandler())
                .setCustomArgumentResolvers(putAuthenticationPrincipal)
                .build();
    }

    //GET
    @Test
    public void findAll_CommentsFound_ShouldReturnFoundComments() throws Exception
    {
        Comment comment1 = new CommentBuilder().id(0L).text("Hello").build();
        Comment comment2 = new CommentBuilder().id(1L).text("Welcome").build();

        Mockito.when(commentRepo.findAll(ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(comment1, comment2)));

        mockMvc.perform(get("/comments")
                .param("page", "0")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(0)))
                .andExpect(jsonPath("$.content[0].text", is("Hello")))
                .andExpect(jsonPath("$.content[1].id", is(1)))
                .andExpect(jsonPath("$.content[1].text", is("Welcome")));

        verify(commentRepo, times(1)).findAll(ArgumentMatchers.any(Pageable.class));
        verifyNoMoreInteractions(commentRepo);
    }

    @Test
    public void findById_CommentNotFound_ShouldReturnHttpStatusCode404() throws Exception {
        mockMvc.perform(get("/comments/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(commentRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(commentRepo);
    }

    @Test
    public void findById_CommentFound_ShouldReturnFoundComment() throws Exception {
        Comment found = new CommentBuilder().id(1L).text("Welcome").build();

        when(commentRepo.findById(1L)).thenReturn(Optional.ofNullable(found));

        mockMvc.perform(get("/comments/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("Welcome")));

        verify(commentRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(commentRepo);
    }

    //POST
    @Test
    @WithMockUser(roles = "USER")
    public void add_CommentWithInvalidFields_ShouldReturnValidationErrors() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        CommentDto commentDto = new CommentDtoBuilder().text(" ").topic(null).build();

        mockMvc.perform(post("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(commentDto))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors", containsInAnyOrder(
                        "Message can't be empty",
                        "Parent topic can't be null"
                )));

        verifyNoInteractions(commentRepo);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void add_CommentWithInvalidParentComment_ShouldReturnForbidden() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        CommentDto commentDto = new CommentDtoBuilder().text("Hello World")
                .topic(new TopicDtoBuilder().id(0L).build())
                .parentComment(new CommentDtoBuilder().id(1L).build())
                .build();

        Comment parent = new CommentBuilder().id(1L).text("Welcome")
                .topic(new TopicBuilder().id(1L).build())
                .build();

        when(commentRepo.findById(1L)).thenReturn(Optional.ofNullable(parent));

        mockMvc.perform(post("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(commentDto))
        )
                .andExpect(status().isNotFound());

        verify(commentRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(commentRepo);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void add_Comment_ShouldAddCommentAndReturnLocationHeader() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        CommentDto commentDto = new CommentDtoBuilder().text("Welcome").build();
        Comment added = new CommentBuilder().id(1L).text("Welcome").build();

        when(commentRepo.save(ArgumentMatchers.any(Comment.class))).thenReturn(added);

        mockMvc.perform(post("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(commentDto))
        )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/comments/1"));

        verify(commentRepo, times(1)).save(ArgumentMatchers.any(Comment.class));
        verifyNoMoreInteractions(commentRepo);
    }

    //PATCH
    @Test
    @WithMockUser(roles = "USER")
    public void patch_CommentNotFound_ShouldReturnHttpStatusCode404() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        CommentPatch patch = new CommentPatch("Hi");

        mockMvc.perform(patch("/comments/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patch))
        )
                .andExpect(status().isNotFound());

        verify(commentRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(commentRepo);
    }

    @Test
    public void patch_CommentWithNoAccess_ShouldReturnForbidden() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        CommentPatch patch = new CommentPatch("Hi");
        Comment patchedComment = new CommentBuilder().id(1L).text("Welcome")
                .user(new UserBuilder().id(2L).build())
                .build();

        when(commentRepo.findById(1L)).thenReturn(Optional.ofNullable(patchedComment));

        mockMvc.perform(patch("/comments/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patch))
        )
                .andExpect(status().isForbidden());

        verify(commentRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(commentRepo);
    }

    @Test
    public void patch_CommentWithInvalidFields_ShouldReturnForbidden() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        CommentPatch patch = new CommentPatch(" ");
        Comment patchedComment = new CommentBuilder().id(1L).text("Welcome")
                .user(new UserBuilder().id(1L).build())
                .build();

        when(commentRepo.findById(1L)).thenReturn(Optional.ofNullable(patchedComment));

        mockMvc.perform(patch("/comments/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patch))
        )
                .andExpect(status().isForbidden());

        verify(commentRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(commentRepo);
    }

    @Test
    public void patch_Comment_ShouldReturnPatchedComment() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        CommentPatch patch = new CommentPatch("Hello");

        Comment patchedComment = new CommentBuilder().id(1L).text("Welcome")
                .user(new UserBuilder().id(1L).build())
                .build();

        Comment returnedComment = new CommentBuilder().id(1L).text("Hello")
                .user(new UserBuilder().id(1L).build())
                .build();

        when(commentRepo.findById(1L)).thenReturn(Optional.ofNullable(patchedComment));
        when(commentRepo.save(ArgumentMatchers.any(Comment.class))).thenReturn(returnedComment);

        mockMvc.perform(patch("/comments/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(patch))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("Hello")));

        verify(commentRepo, times(1)).findById(1L);
        verify(commentRepo, times(1)).save(ArgumentMatchers.any(Comment.class));
        verifyNoMoreInteractions(commentRepo);
    }

    //DELETE
    @Test
    public void delete_CommentWithNoAccess_ShouldReturnForbidden() throws Exception {

        Comment comment = new CommentBuilder().id(1L)
                .user(new UserBuilder().id(2L).build())
                .build();

        when(commentRepo.findById(1L)).thenReturn(Optional.ofNullable(comment));

        mockMvc.perform(delete("/comments/{id}", 1L))
                .andExpect(status().isForbidden());

        verify(commentRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(commentRepo);
    }

    @Test
    public void delete_Comment_ShouldReturnNoContent() throws Exception {

        Comment comment = new CommentBuilder().id(1L)
                .user(new UserBuilder().id(1L).build())
                .build();

        when(commentRepo.findById(1L)).thenReturn(Optional.ofNullable(comment));

        mockMvc.perform(delete("/comments/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(commentRepo, times(1)).findById(1L);
        verify(commentRepo, times(1)).deleteById(1L);
        verifyNoMoreInteractions(commentRepo);
    }

    //GET LIKES
    @Test
    public void findLikesByCommentId_CommentNotFound_ShouldReturnHttpStatusCode404() throws Exception {
        mockMvc.perform(get("/comments/{id}/likes", 1L))
                .andExpect(status().isNotFound());

        verify(commentRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(commentRepo);
        verifyNoInteractions(likeRepo);
    }

    @Test
    public void findLikesByCommentId_Comment_ShouldReturnFoundLikes() throws Exception {

        Comment found = new CommentBuilder().id(1L)
                .build();

        Like like1 = new LikeBuilder().id(0L)
                .comment(new CommentBuilder().id(1L).build())
                .user(new UserBuilder().id(0L).username("user1").build())
                .build();

        Like like2 = new LikeBuilder().id(1L)
                .comment(new CommentBuilder().id(1L).build())
                .user(new UserBuilder().id(1L).username("user2").build())
                .build();

        when(commentRepo.findById(1L)).thenReturn(Optional.ofNullable(found));
        Mockito.when(likeRepo.findAllByCommentId(1L))
                .thenReturn(new PageImpl<>(Arrays.asList(like1, like2)));

        mockMvc.perform(get("/comments/{id}/likes", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(0)))
                .andExpect(jsonPath("$.content[0].user.username", is("user1")))
                .andExpect(jsonPath("$.content[1].id", is(1)))
                .andExpect(jsonPath("$.content[1].user.username", is("user2")));

        verify(commentRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(commentRepo);

        verify(likeRepo, times(1)).findAllByCommentId(1L);
        verifyNoMoreInteractions(likeRepo);
    }

    //DELETE
    @Test
    public void deleteLikeByUserAndCommentId_Comment_ShouldReturnNoContent() throws Exception {

        mockMvc.perform(delete("/comments/{id}/likes", 1L))
                .andExpect(status().isNoContent());

        verify(likeRepo, times(1)).deleteByCommentIdAndUserId(1L, 1L);
        verifyNoMoreInteractions(likeRepo);
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
