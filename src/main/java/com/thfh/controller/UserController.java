package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.LoginDTO;
import com.thfh.dto.UserDTO;
import com.thfh.dto.UserQueryDTO;
import com.thfh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        return Result.success(userService.login(loginDTO));
    }

    @GetMapping("/info")
    public Result<UserDTO> getUserInfo(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        return Result.success(userService.getUserInfo(username));
    }

    @GetMapping
    public Result<Page<UserDTO>> getUsers(UserQueryDTO queryDTO) {
        return Result.success(userService.getUsers(queryDTO));
    }

    @PostMapping
    public Result<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return Result.success(userService.createUser(userDTO));
    }

    @PutMapping("/{id}")
    public Result<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return Result.success(userService.updateUser(id, userDTO));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success(null);
    }

    @PutMapping("/{id}/toggle-status")
    public Result<Void> toggleUserStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return Result.success(null);
    }
}