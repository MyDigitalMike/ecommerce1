package core.ecommerce.controllers;
import core.ecommerce.controller.UserController;
import core.ecommerce.dto.CreateUserRequest;
import core.ecommerce.dto.UserResponse;
import core.ecommerce.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserService userService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    void testGetAllUsers() {
        UserResponse user1 = new UserResponse();
        UserResponse user2 = new UserResponse();
        List<UserResponse> users = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<UserResponse>> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(users, response.getBody());
        verify(userService).getAllUsers();
    }

    @Test
    void testGetUserById() {
        Long userId = 1L;
        UserResponse user = new UserResponse();

        when(userService.getUserById(userId)).thenReturn(user);

        ResponseEntity<UserResponse> response = userController.getUserById(userId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(user, response.getBody());
        verify(userService).getUserById(userId);
    }

    @Test
    void testUpdateUser() {
        Long userId = 1L;
        UserResponse request = new UserResponse();
        UserResponse updatedUser = new UserResponse();

        when(userService.updateUser(eq(userId), any(UserResponse.class))).thenReturn(updatedUser);

        ResponseEntity<UserResponse> response = userController.updateUser(userId, request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(updatedUser, response.getBody());
        verify(userService).updateUser(userId, request);
    }

    @Test
    void testDeleteUser() {
        Long userId = 1L;

        doNothing().when(userService).deleteUser(userId);

        ResponseEntity<Void> response = userController.deleteUser(userId);

        assertEquals(204, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(userService).deleteUser(userId);
    }

    @Test
    void testCreateUser() {
        CreateUserRequest request = new CreateUserRequest();
        UserResponse createdUser = new UserResponse();

        when(userService.createUser(request)).thenReturn(createdUser);

        ResponseEntity<UserResponse> response = userController.createUser(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(createdUser, response.getBody());
        verify(userService).createUser(request);
    }
}
