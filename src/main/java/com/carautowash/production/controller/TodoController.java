package com.carautowash.production.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.carautowash.production.entity.Todo;
import com.carautowash.production.service.TodoService;
// import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class TodoController {

  private final TodoService todoService;
  public TodoController(TodoService todoService) {
    this.todoService = todoService;
  }


  // tạo todo mới
  @PostMapping("/todos")
  public ResponseEntity<Todo> createTodo(@RequestBody Todo todo) {
      Todo newTodo = this.todoService.handleCreateTodo(todo);
      // return ResponseEntity.ok().body(todoOptional);  
      return ResponseEntity.ok().body(newTodo);
  }

  // lấy todo theo id
  @GetMapping("/todos/{id}")
  public ResponseEntity<Todo> getTodoById(@PathVariable("id") Long id) {
      Todo todoOptional = this.todoService.handleGetTodoById(id);
      return ResponseEntity.status(HttpStatus.CREATED).body(todoOptional);  
  }

  // lấy tất cả todo
  @GetMapping("/todos")
  public ResponseEntity<List<Todo>> getAllTodos() {
      // Implementation for fetching all todos
      List<Todo> todos = this.todoService.handleGetTodo();
      todos.forEach(todo -> System.out.println(todo));
      return ResponseEntity.ok().body(todos);
  }

  // cập nhập todo
  @PutMapping("/todos/{id}")
  public ResponseEntity<String> updateTodo(@RequestBody Todo todo, @PathVariable("id") Long id) {
      // Implementation for updating a todo
      String result = this.todoService.handleUpdateTodo(id, todo);
      return ResponseEntity.ok().body(result);
  }
  // cập nhập todo
  @DeleteMapping("/todos/{id}")
  public ResponseEntity<String> deleteTodo(@PathVariable("id") Long id) {
      // Implementation for updating a todo
      String result = this.todoService.handleDeleteTodo(id);
      return ResponseEntity.ok().body(result);
  }
  
}
