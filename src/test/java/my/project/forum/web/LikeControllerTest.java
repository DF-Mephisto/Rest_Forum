package my.project.forum.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.project.forum.data.builder.dto.CommentDtoBuilder;
import my.project.forum.data.builder.dto.LikeDtoBuilder;
import my.project.forum.data.builder.entity.*;
import my.project.forum.data.postgres.dto.LikeDto;
import my.project.forum.data.postgres.entity.Like;
import my.project.forum.data.postgres.repository.LikeRepository;
import my.project.forum.security.UserRepositoryUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = LikeController.class)
public class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepositoryUserDetailsService userRepoService;

    @MockBean
    private LikeRepository likeRepo;

    @BeforeEach
    public void setUp()
    {

    }

    //GET
    @Test
    public void findAll_LikesFound_ShouldReturnFoundLikes() throws Exception
    {
        Like like1 = new LikeBuilder().id(0L)
                .comment(new CommentBuilder().id(1L).build())
                .user(new UserBuilder().id(0L).username("user1").build())
                .build();

        Like like2 = new LikeBuilder().id(1L)
                .comment(new CommentBuilder().id(1L).build())
                .user(new UserBuilder().id(1L).username("user2").build())
                .build();

        Mockito.when(likeRepo.findAll()).thenReturn(Arrays.asList(like1, like2));

        mockMvc.perform(get("/likes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(0)))
                .andExpect(jsonPath("$[0].user.username", is("user1")))
                .andExpect(jsonPath("$[0].comment.id", is(1)))
                .andExpect(jsonPath("$[1].id", is(1)))
                .andExpect(jsonPath("$[1].user.username", is("user2")))
                .andExpect(jsonPath("$[1].comment.id", is(1)));

        verify(likeRepo, times(1)).findAll();
        verifyNoMoreInteractions(likeRepo);
    }

    //POST
    @Test
    @WithMockUser(roles = "USER")
    public void add_LikeWithInvalidFields_ShouldReturnValidationErrors() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        LikeDto likeDto = new LikeDtoBuilder().comment(null).build();

        mockMvc.perform(post("/likes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(likeDto))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", containsInAnyOrder(
                        "Comment can't be null"
                )));

        verifyNoInteractions(likeRepo);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void add_Like_ShouldAddLikeAndReturnAddedLike() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        LikeDto likeDto = new LikeDtoBuilder()
                .comment(new CommentDtoBuilder().id(1L).text("Hello").build())
                .build();

        Like added = new LikeBuilder().id(1L)
                .comment(new CommentBuilder().id(1L).text("Hello").build())
                .build();

        when(likeRepo.save(ArgumentMatchers.any(Like.class))).thenReturn(added);

        mockMvc.perform(post("/likes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(likeDto))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.comment.id", is(1)))
                .andExpect(jsonPath("$.comment.text", is("Hello")));

        verify(likeRepo, times(1)).save(ArgumentMatchers.any(Like.class));
        verifyNoMoreInteractions(likeRepo);
    }
}
