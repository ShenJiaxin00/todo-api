package com.example.demo.todo;

import com.example.demo.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
    private final TodoRepository todoRepository;

    public TodoController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public static class CreateTodoRequest {
        public String title;
        public String description;
    }

    public record TodoResponse(Long id, String title, String description) {
        public static TodoResponse from(Todo t) {
            return new TodoResponse(t.getId(), t.getTitle(), t.getDescription());
        }
    }

    @PostMapping
    public TodoResponse create(@RequestBody CreateTodoRequest req,
                               @AuthenticationPrincipal User user) {
        Todo t = new Todo();
        t.setTitle(req.title);
        t.setDescription(req.description);
        t.setUser(user);
        todoRepository.save(t);
        return TodoResponse.from(t);
    }

    @GetMapping
    public List<TodoResponse> list(@AuthenticationPrincipal User user) {
        return todoRepository.findByUserOrderByCreatedAtDesc(user)
                .stream().map(TodoResponse::from).toList();
    }
}
