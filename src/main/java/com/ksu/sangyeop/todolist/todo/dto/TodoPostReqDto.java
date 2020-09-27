package com.ksu.sangyeop.todolist.todo.dto;

import com.ksu.sangyeop.todolist.todo.Todo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TodoPostReqDto {
    private String description;

    public Todo toEntity() {
        return Todo.builder()
                .description(this.description)
                .status(false)
                .createdDate(LocalDateTime.now()).build();
    }
}
