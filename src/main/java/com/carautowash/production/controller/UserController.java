package com.carautowash.production.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.carautowash.production.entity.ApiResponse;
import com.carautowash.production.entity.User;
import com.carautowash.production.service.UserService;

import jakarta.validation.Valid;

@RestController
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/users")
	public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody User user) {
		User created = userService.createUser(user);

		var result = new ApiResponse<>(HttpStatus.CREATED, "User created successfully", created, null);
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	@GetMapping("/users")
	public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
		var response = new ApiResponse<>(HttpStatus.OK, "Users fetched successfully", userService.getAllUsers(), null);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/users/{id}")
		public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
			return userService.getUserById(id).map(user -> {
					var response = new ApiResponse<>(HttpStatus.OK, "getUserById", user, null);
					return ResponseEntity.ok(response);
			}).orElseGet(() -> {
				ApiResponse<User> errorResponse = new ApiResponse<>(HttpStatus.NOT_FOUND,
				"Khong tim thấy user voi ID: " + id, null, "USER_NOT_FOUND");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
			});
	}

	@PutMapping("/users/{id}")
	public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @RequestBody User user) {
		User updated = userService.updateUser(id, user);
		var response = new ApiResponse<>(HttpStatus.OK, "User updated successfully", updated, null);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/users/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}
}