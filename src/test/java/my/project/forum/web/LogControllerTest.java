package my.project.forum.web;

import my.project.forum.data.builder.entity.LogBuilder;
import my.project.forum.data.mongodb.entity.Log;
import my.project.forum.data.mongodb.repository.LogRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = LogController.class)
public class LogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepositoryUserDetailsService userRepoService;

    @MockBean
    private LogRepository logRepo;

    @MockBean
    private Properties props;

    @BeforeEach
    public void setUp()
    {
        Mockito.when(props.getLogPageSize()).thenReturn(5);
    }

    //GET
    @Test
    @WithMockUser(roles = "ADMIN")
    public void findAll_LogsFound_ShouldReturnFoundLogs() throws Exception
    {
        Log log1 = new LogBuilder().id("0").username("user1").desc("test log 1").build();
        Log log2 = new LogBuilder().id("1").username("user2").desc("test log 2").build();

        Mockito.when(logRepo.findAll(ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(log1, log2)));

        mockMvc.perform(get("/log")
                .param("page", "0")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is("0")))
                .andExpect(jsonPath("$.content[0].username", is("user1")))
                .andExpect(jsonPath("$.content[0].desc", is("test log 1")))
                .andExpect(jsonPath("$.content[1].id", is("1")))
                .andExpect(jsonPath("$.content[1].username", is("user2")))
                .andExpect(jsonPath("$.content[1].desc", is("test log 2")));

        verify(logRepo, times(1)).findAll(ArgumentMatchers.any(Pageable.class));
        verifyNoMoreInteractions(logRepo);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void findById_LogNotFound_ShouldReturnHttpStatusCode404() throws Exception {
        mockMvc.perform(get("/log/{id}", "0"))
                .andExpect(status().isNotFound());

        verify(logRepo, times(1)).findById("0");
        verifyNoMoreInteractions(logRepo);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void findById_LogFound_ShouldReturnFoundLog() throws Exception {
        Log found = new LogBuilder().id("0").username("user1").desc("test log 1").build();

        when(logRepo.findById("0")).thenReturn(Optional.ofNullable(found));

        mockMvc.perform(get("/log/{id}", "0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("0")))
                .andExpect(jsonPath("$.username", is("user1")))
                .andExpect(jsonPath("$.desc", is("test log 1")));

        verify(logRepo, times(1)).findById("0");
        verifyNoMoreInteractions(logRepo);
    }

    //DELETE
    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_Log_ShouldReturnNoContent() throws Exception {

        mockMvc.perform(delete("/log/{id}", "0"))
                .andExpect(status().isNoContent());

        verify(logRepo, times(1)).deleteById("0");
        verifyNoMoreInteractions(logRepo);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteAll_Log_ShouldReturnNoContent() throws Exception {

        mockMvc.perform(delete("/log/clear"))
                .andExpect(status().isNoContent());

        verify(logRepo, times(1)).deleteAll();
        verifyNoMoreInteractions(logRepo);
    }
}
