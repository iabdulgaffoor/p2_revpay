package com.rev.app.service;

import com.rev.app.dto.UserDTO;
import com.rev.app.entity.User;
import com.rev.app.entity.User.Role;
import com.rev.app.exception.BadRequestException;
import com.rev.app.exception.InvalidCredentialsException;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.UserMapper;
import com.rev.app.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IUserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final IWalletService walletService; // To create wallet upon registration

    @Autowired
    public IUserServiceImpl(IUserRepository userRepository, UserMapper userMapper,
            PasswordEncoder passwordEncoder, IWalletService walletService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.walletService = walletService;
    }

    @Override
    @Transactional
    public UserDTO registerUser(UserDTO userDTO, String password) {
        log.info("Attempting to register personal user with email: {}", userDTO.getEmail());
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            log.warn("Registration failed: Email {} already registered", userDTO.getEmail());
            throw new BadRequestException("Email already registered!");
        }
        if (userRepository.findByPhoneNumber(userDTO.getPhoneNumber()).isPresent()) {
            log.warn("Registration failed: Phone {} already registered", userDTO.getPhoneNumber());
            throw new BadRequestException("Phone number already registered!");
        }

        User user = userMapper.toEntity(userDTO);
        user.setRole(Role.PERSONAL);
        user.setPassword(passwordEncoder.encode(password));

        if (userDTO.getTransactionPin() != null && !userDTO.getTransactionPin().isEmpty()) {
            user.setTransactionPin(passwordEncoder.encode(userDTO.getTransactionPin()));
        }

        User savedUser = userRepository.save(user);

        // Create initial empty wallet
        walletService.createWallet(savedUser.getId());

        log.info("Successfully registered personal user with ID: {}", savedUser.getId());
        return userMapper.toDTO(savedUser);
    }

    @Override
    @Transactional
    public UserDTO registerBusinessUser(UserDTO userDTO, String password) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new BadRequestException("Email already registered!");
        }

        User user = userMapper.toEntity(userDTO);
        user.setRole(Role.BUSINESS);
        user.setPassword(passwordEncoder.encode(password));
        user.setIsBusinessVerified(false); // Pending verification ideally

        if (userDTO.getTransactionPin() != null && !userDTO.getTransactionPin().isEmpty()) {
            user.setTransactionPin(passwordEncoder.encode(userDTO.getTransactionPin()));
        }

        User savedUser = userRepository.save(user);

        // Create initial empty wallet
        walletService.createWallet(savedUser.getId());

        return userMapper.toDTO(savedUser);
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    public UserDTO getUserById(Long id) {
        log.info("Fetching user from database for ID: {}", id);
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserDTO> getAllUsersPaginated(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> usersPage = userRepository.searchUsers(search, pageable);
        return usersPage.map(userMapper::toDTO);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        existingUser.setFullName(userDTO.getFullName());
        existingUser.setPhoneNumber(userDTO.getPhoneNumber());
        existingUser.setSecurityQuestion(userDTO.getSecurityQuestion());

        // Business updates
        if (existingUser.getRole() == Role.BUSINESS) {
            existingUser.setBusinessName(userDTO.getBusinessName());
            existingUser.setBusinessType(userDTO.getBusinessType());
            existingUser.setTaxId(userDTO.getTaxId());
            existingUser.setBusinessAddress(userDTO.getBusinessAddress());
        }

        User savedUser = userRepository.save(existingUser);

        return userMapper.toDTO(savedUser);
    }

    @Override
    @Transactional
    public UserDTO adminCreateUser(UserDTO userDTO, String password, Role role) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new BadRequestException("Email already registered!");
        }

        User user = userMapper.toEntity(userDTO);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(password));

        if (userDTO.getIsActive() != null) {
            user.setIsActive(userDTO.getIsActive());
        }

        if (role == Role.BUSINESS) {
            user.setIsBusinessVerified(
                    userDTO.getIsBusinessVerified() != null ? userDTO.getIsBusinessVerified() : true);
        }

        if (userDTO.getTransactionPin() != null && !userDTO.getTransactionPin().isEmpty()) {
            user.setTransactionPin(passwordEncoder.encode(userDTO.getTransactionPin()));
        }

        User savedUser = userRepository.save(user);
        walletService.createWallet(savedUser.getId());

        return userMapper.toDTO(savedUser);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public UserDTO adminUpdateUser(Long id, UserDTO userDTO, String newPassword, Role role, boolean isActive) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        existingUser.setFullName(userDTO.getFullName());
        existingUser.setPhoneNumber(userDTO.getPhoneNumber());
        existingUser.setRole(role);
        existingUser.setIsActive(isActive);

        if (newPassword != null && !newPassword.trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(newPassword));
        }

        if (role == Role.BUSINESS) {
            existingUser.setBusinessName(userDTO.getBusinessName());
            existingUser.setBusinessType(userDTO.getBusinessType());
            existingUser.setTaxId(userDTO.getTaxId());
            existingUser.setBusinessAddress(userDTO.getBusinessAddress());
            existingUser.setIsBusinessVerified(userDTO.getIsBusinessVerified());
        }

        User savedUser = userRepository.save(existingUser);
        return userMapper.toDTO(savedUser);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public boolean verifyPassword(Long userId, String currentPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        return passwordEncoder.matches(currentPassword, user.getPassword());
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            log.warn("Password update failed for user ID {}: Invalid current password", userId);
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password successfully updated for user ID: {}", userId);
    }

    @Override
    @Transactional
    public void updateTransactionPin(Long userId, String newPin) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        // In a real app, PINs should be hashed as well. We will use the passwordEncoder
        // for the PIN for security.
        user.setTransactionPin(passwordEncoder.encode(newPin));
        userRepository.save(user);
    }

    @Override
    public UserDTO getUserByIdentifier(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new BadRequestException("Identifier cannot be empty");
        }

        identifier = identifier.trim();

        // 1. Try to parse as Account ID (Long)
        try {
            Long id = Long.parseLong(identifier);
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isPresent()) {
                return userMapper.toDTO(userOpt.get());
            }
        } catch (NumberFormatException e) {
            // Not a Long, continue to other checks
        }

        // 2. Try Email
        Optional<User> byEmail = userRepository.findByEmail(identifier);
        if (byEmail.isPresent()) {
            return userMapper.toDTO(byEmail.get());
        }

        // 3. Try Phone Number
        Optional<User> byPhone = userRepository.findByPhoneNumber(identifier);
        if (byPhone.isPresent()) {
            return userMapper.toDTO(byPhone.get());
        }

        // 4. Try Full Name (exact match case-insensitive)
        Optional<User> byName = userRepository.findByFullNameIgnoreCase(identifier);
        if (byName.isPresent()) {
            return userMapper.toDTO(byName.get());
        }

        throw new ResourceNotFoundException("User not found with identifier: " + identifier);
    }

}
