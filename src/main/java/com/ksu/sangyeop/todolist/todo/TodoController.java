package com.ksu.sangyeop.todolist.todo;

import com.ksu.sangyeop.todolist.todo.dto.TodoPostReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/todos", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TodoController {

    private final TodoRepository todoRepository;

    @GetMapping
    public ResponseEntity<?> getTodos() {
        List<Todo> tdl = todoRepository.findAll();
        return new ResponseEntity<>(tdl, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> postTodos(@RequestBody TodoPostReqDto req) {
        Todo savedTodo = todoRepository.save(req.toEntity());
        return new ResponseEntity<>(savedTodo, HttpStatus.CREATED);
    }
}
