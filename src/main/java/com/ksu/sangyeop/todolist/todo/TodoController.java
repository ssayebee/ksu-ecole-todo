package com.ksu.sangyeop.todolist.todo;

import com.ksu.sangyeop.todolist.todo.dto.TodoReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = "/api/todos", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class TodoController {

    private final TodoRepository todoRepository;

    @GetMapping
    public ResponseEntity<?> getTodos(Pageable pageable, PagedResourcesAssembler<Todo> assembler) {
        Page<Todo> todoList = todoRepository.findAll(pageable);
        PagedModel<EntityModel<Todo>> pagedModel = assembler.toModel(todoList, todo -> {
            WebMvcLinkBuilder self = WebMvcLinkBuilder.linkTo(TodoController.class).slash(todo.getIdx());
            return EntityModel.of(todo)
                    .add(self.withRel("load-todo"))
                    .add(self.slash("status").withRel("toggle-todo"));
        });
        pagedModel.add(WebMvcLinkBuilder.linkTo(TodoController.class).withRel("post-todo"));
        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }

    @GetMapping("/{idx}")
    public ResponseEntity<?> getTodo(@PathVariable Long idx) {
        Optional<Todo> optionalTodo = todoRepository.findById(idx);
        if(!optionalTodo.isPresent()) {
            return new ResponseEntity<>("존재하지 않는 Todo", HttpStatus.BAD_REQUEST);
        } else {
            Todo todo = optionalTodo.get();
            WebMvcLinkBuilder self = WebMvcLinkBuilder.linkTo(TodoController.class).slash(todo.getIdx());
            EntityModel<Todo> entityModel = EntityModel.of(todo)
                    .add(self.withRel("put-todo"))
                    .add(self.withRel("delete-todo"))
                    .add(WebMvcLinkBuilder.linkTo(TodoController.class).withRel("load-todo-list"))
                    .add(self.withSelfRel());
            return new ResponseEntity<>(entityModel, HttpStatus.OK);
        }

    }

    @PostMapping
    public ResponseEntity<?> postTodos(@RequestBody TodoReqDto req) {
        Todo savedTodo = todoRepository.save(req.toEntity());
        WebMvcLinkBuilder self = WebMvcLinkBuilder.linkTo(TodoController.class).slash(savedTodo.getIdx());
        EntityModel<Todo> entityModel = EntityModel.of(savedTodo)
                .add(WebMvcLinkBuilder.linkTo(TodoController.class).withRel("load-todo-list"))
                .add(self.withSelfRel());
        return new ResponseEntity<>(entityModel, HttpStatus.CREATED);
    }

    @PutMapping("/{idx}/status")
    public ResponseEntity<?> toggleTodo(@PathVariable Long idx) {
        Optional<Todo> optionalTodo = todoRepository.findById(idx);
        if(!optionalTodo.isPresent()) {
            return new ResponseEntity<>("존재하지 않는 Todo", HttpStatus.BAD_REQUEST);
        } else {
            Todo todo = optionalTodo.get();
            todo.toggleTodo();
            Todo savedTodo = todoRepository.save(todo);
            WebMvcLinkBuilder self = WebMvcLinkBuilder.linkTo(TodoController.class)
                    .slash(savedTodo.getIdx())
                    .slash("status");
            EntityModel<Todo> entityModel = EntityModel.of(savedTodo)
                    .add(WebMvcLinkBuilder.linkTo(TodoController.class).withRel("load-todo-list"))
                    .add(self.withSelfRel());
            return new ResponseEntity<>(entityModel, HttpStatus.OK);
        }
    }

    @PutMapping("/{idx}")
    public ResponseEntity<?> putTodo(@PathVariable Long idx, @RequestBody TodoReqDto req) {
        Optional<Todo> optionalTodo = todoRepository.findById(idx);
        if(!optionalTodo.isPresent()) {
            return new ResponseEntity<>("존재하지 않는 Todo", HttpStatus.BAD_REQUEST);
        } else {
            Todo todo = optionalTodo.get();
            System.out.println("여기다 여기 쮸발 쮸발" + req.getDescription());
            todo.putTodo(req);
            Todo savedTodo = todoRepository.save(todo);
            WebMvcLinkBuilder self = WebMvcLinkBuilder.linkTo(TodoController.class).slash(savedTodo.getIdx());
            EntityModel<Todo> entityModel = EntityModel.of(savedTodo)
                    .add(self.withRel("load-todo"))
                    .add(WebMvcLinkBuilder.linkTo(TodoController.class).withRel("load-todo-list"))
                    .add(self.withSelfRel());
            return new ResponseEntity<>(entityModel, HttpStatus.OK);
        }
    }

    @DeleteMapping("/{idx}")
    public ResponseEntity<?> deleteTodo(@PathVariable Long idx) {
        Optional<Todo> optionalTodo = todoRepository.findById(idx);
        if (!optionalTodo.isPresent()) {
            return new ResponseEntity<>("존재하지 않는 Todo", HttpStatus.BAD_REQUEST);
        } else {
            Todo todo = optionalTodo.get();
            todoRepository.deleteById(todo.getIdx());
            WebMvcLinkBuilder self = WebMvcLinkBuilder.linkTo(TodoController.class).slash(todo.getIdx());
            EntityModel<Todo> entityModel = EntityModel.of(todo)
                    .add(WebMvcLinkBuilder.linkTo(TodoController.class).withRel("load-todo-list"))
                    .add(self.withSelfRel());
            return new ResponseEntity<>(entityModel, HttpStatus.OK);
        }
    }
}
