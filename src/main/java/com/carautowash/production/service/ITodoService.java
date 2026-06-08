package com.carautowash.production.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.carautowash.production.entity.Todo;

@Service
public interface ITodoService {
  // final TodoRepository todoRepository;

  public Todo handleCreateTodo(Todo todo);

  public String handleUpdateTodo(Long id, Todo todo);
  public List<Todo> handleGetTodo() ;
  public Todo handleGetTodoById(Long id);
  public String handleDeleteTodo(Long id);
}
