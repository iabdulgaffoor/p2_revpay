package com.rev.app.service;

import com.rev.app.dto.UserDTO;
import java.util.List;
import org.springframework.data.domain.Page;

public interface IUserService {
    UserDTO registerUser(UserDTO userDTO, String password);

    UserDTO registerBusinessUser(UserDTO userDTO, String password);

    UserDTO getUserById(Long id);

    UserDTO getUserByEmail(String email);

    List<UserDTO> getAllUsers();

    Page<UserDTO> getAllUsersPaginated(int page, int size, String sortBy, String sortDir, String search);

    UserDTO updateUser(Long id, UserDTO userDTO);

    UserDTO adminCreateUser(UserDTO userDTO, String password, com.rev.app.entity.User.Role role);

    UserDTO adminUpdateUser(Long id, UserDTO userDTO, String newPassword, com.rev.app.entity.User.Role role,
            boolean isActive);

    void deleteUser(Long id);

    boolean verifyPassword(Long userId, String currentPassword);

    void updatePassword(Long userId, String currentPassword, String newPassword);

    void updateTransactionPin(Long userId, String newPin);

    UserDTO getUserByIdentifier(String identifier);
}
