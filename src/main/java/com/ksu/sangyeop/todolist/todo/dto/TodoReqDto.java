package com.ksu.sangyeop.todolist.todo.dto;

import com.ksu.sangyeop.todolist.todo.Todo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoReqDto {
    private String description;

    private Boolean status;

    public Todo toEntity() {
        return Todo.builder()
                .description(this.description)
                .status(false)
                .createdDate(LocalDateTime.now())
                .build();
    }
}
