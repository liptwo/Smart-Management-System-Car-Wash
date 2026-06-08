package com.carautowash.production.controller.errors;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.carautowash.production.entity.ApiResponse;

@RestControllerAdvice
public class GlobalException {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
      List<String> errorList = ex.getBindingResult().getFieldErrors().stream()
              .map(error -> error.getField() + ": " + error.getDefaultMessage())
              .collect(Collectors.toList());
      String errors = String.join("; ", errorList);
      
      ApiResponse<Object> response = new ApiResponse<>(HttpStatus.BAD_REQUEST, errors, null, "VALIDATION_ERROR");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  
  // Bắt IllegalArgumentException và trả về response với cấu trúc ApiResponse
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<?>> handleIllegalArgument(IllegalArgumentException ex) {
    var response = new ApiResponse<>(HttpStatus.BAD_REQUEST, ex.getMessage(), null, "INVALID_ARGUMENT");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  // Bắt tất cả các exception chưa config và trả về response với cấu trúc ApiResponse
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<?>> handleIllegalArgument(Exception ex) {
    var response = new ApiResponse<>(HttpStatus.BAD_REQUEST, ex.getMessage(), null, "INVALID_ARGUMENT");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }
}
