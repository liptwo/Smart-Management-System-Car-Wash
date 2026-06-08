package com.carautowash.production.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.carautowash.production.entity.Todo;
import com.carautowash.production.repository.TodoRepository;

@Service
public class TodoService implements ITodoService {
  final TodoRepository todoRepository;

  public TodoService(TodoRepository todoRepository) {
    this.todoRepository = todoRepository;
  }

  @Override
  public Todo handleCreateTodo(Todo todo) {
    // Logic to create a todo item
    System.out.println("Creating a new todo item...");
    Todo createdTodo = this.todoRepository.save(todo);
    return createdTodo;
  }

  @Override
  public String handleUpdateTodo(Long id, Todo todo) {
    // Logic to update a todo item
    Todo existingTodo = this.todoRepository.findById(id).orElse(null);
    if (existingTodo != null) {
        existingTodo.setTitle(todo.getTitle());
        existingTodo.setCompleted(todo.isCompleted());
        this.todoRepository.save(existingTodo);
        
        return "Todo updated successfully";
    }
    return "Todo not found with id: " + id;
  }

  @Override
  public List<Todo> handleGetTodo() {
    return this.todoRepository.findAll();
  }

  @Override
  public Todo handleGetTodoById(Long id) {
    Optional<Todo> todoOptional = this.todoRepository.findById(id);
    return todoOptional.isPresent() ? todoOptional.get() : null;
  }

  @Override
  public String handleDeleteTodo(Long id) {
    this.todoRepository.deleteById(id); // Assuming you have a way to identify the todo to delete
    return "Todo deleted successfully";
  }
}
