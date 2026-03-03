package com.rev.app.rest;

import com.rev.app.dto.UserDTO;
import com.rev.app.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final IUserService userService;

    @Autowired
    public UserRestController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/email")
    public ResponseEntity<UserDTO> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("/identifier")
    public ResponseEntity<UserDTO> getUserByIdentifier(@RequestParam String identifier) {
        return ResponseEntity.ok(userService.getUserByIdentifier(identifier));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestBody Map<String, String> passwords) {
        userService.updatePassword(id, passwords.get("currentPassword"), passwords.get("newPassword"));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/pin")
    public ResponseEntity<Void> updateTransactionPin(@PathVariable Long id, @RequestBody Map<String, String> request) {
        userService.updateTransactionPin(id, request.get("newPin"));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/preferences")
    public ResponseEntity<UserDTO> updateNotificationPreferences(@PathVariable Long id, @RequestBody Map<String, Boolean> prefs) {
        boolean transactionAlerts = prefs.getOrDefault("transactionAlerts", true);
        boolean securityAlerts = prefs.getOrDefault("securityAlerts", true);
        
        return ResponseEntity.ok(userService.updateNotificationPreferences(id, transactionAlerts, securityAlerts));
    }
}
