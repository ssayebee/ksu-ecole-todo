package com.ksu.sangyeop.todolist.todo;

import com.ksu.sangyeop.todolist.todo.dto.TodoReqDto;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table
public class Todo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private String description;

    private Boolean status;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    public void toggleTodo() {
        this.status = !this.status;
        this.updatedDate = LocalDateTime.now();
    }

    public void putTodo(TodoReqDto req) {
        this.description = req.getDescription();
        this.status = req.getStatus();
        this.updatedDate = LocalDateTime.now();
    }

}
