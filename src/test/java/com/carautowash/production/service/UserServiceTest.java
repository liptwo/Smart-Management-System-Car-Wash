package com.carautowash.production.service;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.carautowash.production.entity.User;
import com.carautowash.production.repository.UserRepository;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  // fake UserRepository để kiểm tra UserService mà không cần kết nối đến cơ sở dữ liệu thực tế
  @Mock
  private UserRepository userRepository;

  // InjectMocks sẽ tạo một instance của UserService và inject mock UserRepository vào đó
  @InjectMocks
  private UserService userService;

  @Test
  public void createUser_ShouldReturnCreatedUser_WhenEmailValid(){
    //arrange: chuan bi
    User inputUser = new User(null, "John Doe", "liptwo@gmail.com");
    User outputUser = new User(1L, "John Doe", "liptwo@gmail.com");
    when(this.userRepository.existsByEmail(inputUser.getEmail())).thenReturn(false);

    when(this.userRepository.save(any())).thenReturn(outputUser);
    //act: hanh dong
    User result = this.userService.createUser(inputUser);

    // assert: kiem tra ket qua
    assertEquals(1L, result.getId(), "Expected the created user id to be returned");
  }

  @Test
  public void createUser_ShouldReturnThrowException_WhenEmailValid(){
    //arrange: chuan bi
    User inputUser = new User(null, "John Doe", "liptwo@gmail.com");
    // User outputUser = new User(1L, "John Doe", "liptwo@gmail.com");
    when(this.userRepository.existsByEmail(inputUser.getEmail())).thenReturn(true);

    // when(this.userRepository.save(any())).thenReturn(outputUser);
    //act: hanh dong
    Exception ex = assertThrows(IllegalArgumentException.class, () -> {
      this.userService.createUser(inputUser);
    });
    // assert: kiem tra ket qua
    assertEquals("Email already exists", ex.getMessage());
  }

  @Test
  public void getAllUsers_ShouldReturnAllUser(){
    //arrange: chuan bi
    List<User> outputUsers = List.of(
      new User(1L, "John Doe", "liptwo@gmail.com"),
      new User(2L, "Jane Doe", "liptwo1@gmail.com"));
    when(this.userRepository.findAll()).thenReturn(outputUsers);
    //act: hanh dong
    List<User> result = this.userService.getAllUsers();
    // assert: kiem tra ket qua
    assertEquals(2, result.size(), "Expected the list of users to be returned");
  }
  @Test
  public void updateUser_ShouldReturnUpdateUser(){
    //arrange: chuan bi
    Long userId = 1L;
    User existingUser = new User(userId, "John Doe", "liptwo@gmail");
    User updatedUser = new User(userId, "John Smith", "liptwo@gmail");
    when(this.userRepository.findById(userId)).thenReturn(java.util.Optional.of(existingUser));
    when(this.userRepository.save(any())).thenReturn(updatedUser);
    //act: hanh dong
    User result = this.userService.updateUser(userId, updatedUser);
    // assert: kiem tra ket qua
    assertEquals("John Smith", result.getName(), "Expected the updated user name to be returned");
  }

  @Test
  public void getUserById_ShouldReturnOptionalUser(){
    //arrange: chuan bi
    Long userId = 1L;
    User existingUser = new User(userId, "John Doe", "liptwo@gmail");
    when(this.userRepository.findById(userId)).thenReturn(java.util.Optional.of(existingUser));
     //act: hanh dong
     java.util.Optional<User> result = this.userService.getUserById(userId);
     // assert: kiem tra ket qua
     assertEquals(true, result.isPresent(), "Expected the user to be found");
     assertEquals("John Doe", result.get().getName(), "Expected the user name to be returned");

  }
  
  @Test
  public void deleteUser_ShouldReturnVoid_WhenUserExist(){
    //arrange: chuan bi
    Long userId = 1L;
    when(this.userRepository.existsById(userId)).thenReturn(true);
     //act: hanh dong
     this.userService.deleteUser(userId);
     // assert: kiem tra ket qua
     Mockito.verify(this.userRepository).deleteById(userId);
  }
  @Test
  public void deleteUser_ShouldReturnException_WhenUserNotExist(){
    //arrange: chuan bi
    Long userId = 1L;
    when(this.userRepository.existsById(userId)).thenReturn(false);
      //act: hanh dong
      Exception ex = assertThrows(NoSuchElementException.class, () -> {
        this.userService.deleteUser(userId);
      });
    // assert: kiem tra ket qua
    assertEquals("User not found", ex.getMessage());
  }

  @Test
  public void _ShouldReturn_When(){
    //arrange: chuan bi

    //act: hanh dong

    // assert: kiem tra ket qua
  }
}
