package com.ksu.sangyeop.todolist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksu.sangyeop.todolist.todo.Todo;
import com.ksu.sangyeop.todolist.todo.TodoRepository;
import com.ksu.sangyeop.todolist.todo.dto.TodoReqDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DisplayName("Todo Controller 테스트")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TodoControllerTest {

    @Autowired
    private ObjectMapper objMapper;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;


    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter(StandardCharsets.UTF_8.name()))
                .build()
        ;

        IntStream.rangeClosed(1, 5).forEach(i -> {
            TodoReqDto req = TodoReqDto.builder().description("할일 목록" + i).build();
            try {
                mockMvc.perform(post("/api/todos")
                        .content(objMapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                            .andExpect(status().isCreated())
                ;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @AfterEach
    public void reset() {
        todoRepository.deleteAll();
    }

    @Test
    @DisplayName("todo 등록 테스트")
    public void postTodo() throws Exception {

        TodoReqDto req = TodoReqDto.builder().description("할일 목록").build();
        mockMvc.perform(post("/api/todos")
                .content(objMapper.writeValueAsString(req))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON))
                    .andDo(print())
                    .andExpect(status().isCreated())
        ;
    }

    @Test
    @DisplayName("todolist 가져오기 테스트")
    public void getTodos() throws Exception {

        mockMvc.perform(get("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("_embedded.todoList.[*].idx").isArray())
                    .andExpect(jsonPath("_embedded.todoList.[*].description").isArray())
                    .andExpect(jsonPath("_embedded.todoList.[*].status").isArray())
                    .andExpect(jsonPath("_embedded.todoList.[*].createdDate").isArray())
                    .andExpect(jsonPath("_embedded.todoList.[*].updatedDate").isArray())
        ;
    }

    @Test
    @DisplayName("todo 가져오기 테스트")
    public void getTodo() throws Exception {

        mockMvc.perform(get("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("idx").exists())
                    .andExpect(jsonPath("description").exists())
                    .andExpect(jsonPath("status").exists())
                    .andExpect(jsonPath("createdDate").exists())
                    .andExpect(jsonPath("updatedDate").isEmpty())
        ;
    }

    @Test
    @DisplayName("todo toggle 테스트")
    public void toggleTodo() throws Exception {
        Todo todo = todoRepository.findById(1L).get();
        Boolean nowStatus = todo.getStatus();

        mockMvc.perform(put("/api/todos/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("idx").exists())
                    .andExpect(jsonPath("description").exists())
                    .andExpect(jsonPath("status").exists())
                    .andExpect(jsonPath("createdDate").exists())
                    .andExpect(jsonPath("updatedDate").exists())
        ;

        Todo changed =  todoRepository.findById(1L).get();
        assertThat(nowStatus).isEqualTo(!changed.getStatus());
    }

    @Test
    @DisplayName("todo 수정 테스트")
    public void putTodo() throws Exception {

        String description = "수정된 Todo";

        mockMvc.perform(put("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objMapper.writeValueAsString(
                        TodoReqDto.builder().description(description).status(true).build())
                )
                .accept(MediaTypes.HAL_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("idx").exists())
                    .andExpect(jsonPath("description").exists())
                    .andExpect(jsonPath("status").exists())
                    .andExpect(jsonPath("createdDate").exists())
                    .andExpect(jsonPath("updatedDate").exists())
        ;

        Todo changed = todoRepository.findById(1L).get();
        assertThat(description).isEqualTo(changed.getDescription());
        assertThat(true).isEqualTo(changed.getStatus());

    }

    @Test
    @DisplayName("todo 삭제 테스트")
    public void deleteTodo() throws Exception {

        mockMvc.perform(delete("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("idx").exists())
                .andExpect(jsonPath("description").exists())
                .andExpect(jsonPath("status").exists())
                .andExpect(jsonPath("createdDate").exists())
                .andExpect(jsonPath("updatedDate").isEmpty())
        ;

        Optional<Todo> deleted = todoRepository.findById(1L);
        Assertions.assertFalse(deleted.isPresent());

        System.out.println(todoRepository.findAll().size());
    }

}