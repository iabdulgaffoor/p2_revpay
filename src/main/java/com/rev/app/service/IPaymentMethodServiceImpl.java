package com.rev.app.service;

import com.rev.app.dto.PaymentMethodDTO;
import com.rev.app.entity.PaymentMethod;
import com.rev.app.entity.User;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.exception.BadRequestException;
import com.rev.app.mapper.PaymentMethodMapper;
import com.rev.app.repository.IPaymentMethodRepository;
import com.rev.app.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IPaymentMethodServiceImpl implements IPaymentMethodService {

    private final IPaymentMethodRepository paymentMethodRepository;
    private final IUserRepository userRepository;
    private final PaymentMethodMapper paymentMethodMapper;

    @Autowired
    public IPaymentMethodServiceImpl(IPaymentMethodRepository paymentMethodRepository, 
                                     IUserRepository userRepository,
                                     PaymentMethodMapper paymentMethodMapper) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.userRepository = userRepository;
        this.paymentMethodMapper = paymentMethodMapper;
    }

    @Override
    @Transactional
    public PaymentMethodDTO addPaymentMethod(Long userId, PaymentMethodDTO paymentMethodDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<PaymentMethod> existingMethods = paymentMethodRepository.findByUserId(userId);

        PaymentMethod pm = paymentMethodMapper.toEntity(paymentMethodDTO);
        pm.setUser(user);
        
        // Use a dummy encryption/obfuscation strategy for demo
        pm.setAccountNumber(paymentMethodDTO.getAccountNumberMasked()); 
        
        // If this is the user's first payment method, set it as default
        if (existingMethods.isEmpty()) {
            pm.setDefault(true);
        } else if (pm.isDefault()) {
            // Unset previous defaults
            existingMethods.forEach(method -> method.setDefault(false));
            paymentMethodRepository.saveAll(existingMethods);
        } else {
            pm.setDefault(false);
        }

        return paymentMethodMapper.toDTO(paymentMethodRepository.save(pm));
    }

    @Override
    public PaymentMethodDTO getPaymentMethodById(Long id) {
        return paymentMethodRepository.findById(id)
                .map(paymentMethodMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found"));
    }

    @Override
    public List<PaymentMethodDTO> getPaymentMethodsByUserId(Long userId) {
        return paymentMethodRepository.findByUserId(userId).stream()
                .map(paymentMethodMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentMethodDTO updatePaymentMethod(Long id, PaymentMethodDTO paymentMethodDTO) {
        PaymentMethod pm = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found: " + id));

        pm.setExpiryDate(paymentMethodDTO.getExpiryDate());
        pm.setBillingAddress(paymentMethodDTO.getBillingAddress());

        if (paymentMethodDTO.isDefault() && !pm.isDefault()) {
            setDefaultPaymentMethod(id, pm.getUser().getId());
            pm.setDefault(true); // Redundant after setDefaultPaymentMethod call but clear
        }

        return paymentMethodMapper.toDTO(paymentMethodRepository.save(pm));
    }

    @Override
    @Transactional
    public void deletePaymentMethod(Long id, Long userId) {
        PaymentMethod pm = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found: " + id));

        if (!pm.getUser().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized deletion");
        }

        paymentMethodRepository.deleteById(id);
    }

    @Override
    @Transactional
    public PaymentMethodDTO setDefaultPaymentMethod(Long id, Long userId) {
        PaymentMethod pmToSetDefault = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found: " + id));

        if (!pmToSetDefault.getUser().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized access to payment method");
        }

        List<PaymentMethod> methods = paymentMethodRepository.findByUserId(userId);
        for (PaymentMethod pm : methods) {
            pm.setDefault(pm.getId().equals(id));
        }

        paymentMethodRepository.saveAll(methods);
        return paymentMethodMapper.toDTO(pmToSetDefault);
    }
}
