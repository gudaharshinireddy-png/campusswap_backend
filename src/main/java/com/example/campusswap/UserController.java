package com.example.campusswap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class UserController {

    @Autowired
    private UserRepository userRepository;


    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Users user) {

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Name is required");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required");
        }

        if (user.getPassword().length() < 6) {
            return ResponseEntity.badRequest()
                    .body("Password must contain at least 6 characters");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already registered");
        }

        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("STUDENT");
        }

        user.setBlocked(false);

        Users savedUser = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }


    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginData loginData) {

        if (loginData.getEmail() == null || loginData.getPassword() == null) {
            return ResponseEntity.badRequest()
                    .body("Email and password are required");
        }

        Optional<Users> userOptional =
                userRepository.findByEmail(loginData.getEmail());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        Users user = userOptional.get();

        if (user.isBlocked()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Your account has been blocked by the administrator");
        }

        if (!user.getPassword().equals(loginData.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        return ResponseEntity.ok(user);
    }


    // GET ALL USERS
    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {

        return ResponseEntity.ok(userRepository.findAll());
    }


    // GET USER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {

        Optional<Users> user = userRepository.findById(id);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        return ResponseEntity.ok(user.get());
    }


    // UPDATE USER
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody Users updatedUser) {

        Optional<Users> existingUserOptional =
                userRepository.findById(id);

        if (existingUserOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        Users existingUser = existingUserOptional.get();

        if (updatedUser.getName() != null &&
                !updatedUser.getName().trim().isEmpty()) {
            existingUser.setName(updatedUser.getName());
        }

        if (updatedUser.getPhone() != null) {
            existingUser.setPhone(updatedUser.getPhone());
        }

        if (updatedUser.getDepartment() != null) {
            existingUser.setDepartment(updatedUser.getDepartment());
        }

        if (updatedUser.getYear() != null) {
            existingUser.setYear(updatedUser.getYear());
        }

        if (updatedUser.getProfileImage() != null) {
            existingUser.setProfileImage(updatedUser.getProfileImage());
        }

        Users savedUser = userRepository.save(existingUser);

        return ResponseEntity.ok(savedUser);
    }


    // UPDATE PASSWORD
    @PutMapping("/{id}/password")
    public ResponseEntity<?> updatePassword(
            @PathVariable Long id,
            @RequestBody UpdatePassword passwordData) {

        Optional<Users> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        Users user = userOptional.get();

        if (passwordData.getOldPassword() == null ||
                passwordData.getNewPassword() == null) {

            return ResponseEntity.badRequest()
                    .body("Old password and new password are required");
        }

        if (!user.getPassword().equals(passwordData.getOldPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Old password is incorrect");
        }

        if (passwordData.getNewPassword().length() < 6) {
            return ResponseEntity.badRequest()
                    .body("New password must contain at least 6 characters");
        }

        user.setPassword(passwordData.getNewPassword());

        userRepository.save(user);

        return ResponseEntity.ok("Password updated successfully");
    }


    // BLOCK USER
    @PutMapping("/{id}/block")
    public ResponseEntity<?> blockUser(@PathVariable Long id) {

        Optional<Users> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        Users user = userOptional.get();
        user.setBlocked(true);

        userRepository.save(user);

        return ResponseEntity.ok("User blocked successfully");
    }


    // UNBLOCK USER
    @PutMapping("/{id}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable Long id) {

        Optional<Users> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        Users user = userOptional.get();
        user.setBlocked(false);

        userRepository.save(user);

        return ResponseEntity.ok("User unblocked successfully");
    }


    // DELETE USER
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {

        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        userRepository.deleteById(id);

        return ResponseEntity.ok("User deleted successfully");
    }
}