package com.ksu.sangyeop.todolist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksu.sangyeop.todolist.todo.TodoRepository;
import com.ksu.sangyeop.todolist.todo.dto.TodoPostReqDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Todo Controller 테스트")
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objMapper;

    @Autowired
    private TodoRepository todoRepository;

    @Test
    @DisplayName("todo 등록 테스트")
    public void postTodo() throws Exception {
        TodoPostReqDto req = TodoPostReqDto.builder().description("할일 목록").build();
        mockMvc.perform(post("/api/todos")
                .content(objMapper.writeValueAsString(req)).contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("todolist 가져오기 테스트")
    public void getTodos() throws Exception {

        IntStream.rangeClosed(1, 5).forEach(i -> {
            TodoPostReqDto req = TodoPostReqDto.builder().description("할일 목록" + i).build();
            try {
                mockMvc.perform(post("/api/todos")
                        .content(objMapper.writeValueAsString(req)).contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isCreated());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        mockMvc.perform(get("/api/todos"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].idx").isArray())
                .andExpect(jsonPath("$.[*].description").isArray())
                .andExpect(jsonPath("$.[*].status").isArray())
                .andExpect(jsonPath("$.[*].createdDate").isArray())
                .andExpect(jsonPath("$.[*].updatedDate").isArray())
                .andExpect(status().isOk());
    }

}