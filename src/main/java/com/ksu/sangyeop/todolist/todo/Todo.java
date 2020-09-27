package com.ksu.sangyeop.todolist.todo;

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

}
