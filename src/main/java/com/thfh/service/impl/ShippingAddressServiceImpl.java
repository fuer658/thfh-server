package com.thfh.service.impl;

import com.thfh.dto.ShippingAddressDTO;
import com.thfh.exception.ResourceNotFoundException;
import com.thfh.model.ShippingAddress;
import com.thfh.model.User;
import com.thfh.repository.ShippingAddressRepository;
import com.thfh.service.ShippingAddressService;
import com.thfh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 收货地址服务实现类
 */
@Service
public class ShippingAddressServiceImpl implements ShippingAddressService {
    
    @Autowired
    private ShippingAddressRepository shippingAddressRepository;
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取当前用户的所有收货地址
     * @return 收货地址列表
     */
    @Override
    public List<ShippingAddress> getCurrentUserAddresses() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        return shippingAddressRepository.findByUser(currentUser);
    }
    
    /**
     * 根据ID获取收货地址
     * @param id 收货地址ID
     * @return 收货地址
     */
    @Override
    public ShippingAddress getAddressById(Long id) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        
        return shippingAddressRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("收货地址不存在"));
    }
    
    /**
     * 获取当前用户的默认收货地址
     * @return 默认收货地址，如果不存在则返回null
     */
    @Override
    public ShippingAddress getDefaultAddress() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        
        return shippingAddressRepository.findByUserAndIsDefaultTrue(currentUser).orElse(null);
    }
    
    /**
     * 添加收货地址
     * @param addressDTO 收货地址DTO
     * @return 添加后的收货地址
     */
    @Override
    @Transactional
    public ShippingAddress addAddress(ShippingAddressDTO addressDTO) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        
        ShippingAddress address = new ShippingAddress();
        address.setUser(currentUser);
        address.setReceiverName(addressDTO.getReceiverName());
        address.setReceiverPhone(addressDTO.getReceiverPhone());
        address.setAddress(addressDTO.getAddress());
        address.setIsDefault(addressDTO.getIsDefault());
        
        // 如果设置为默认地址，需要将其他地址设置为非默认
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            shippingAddressRepository.clearDefaultAddress(currentUser.getId());
        }
        
        return shippingAddressRepository.save(address);
    }
    
    /**
     * 更新收货地址
     * @param addressDTO 收货地址DTO
     * @return 更新后的收货地址
     */
    @Override
    @Transactional
    public ShippingAddress updateAddress(ShippingAddressDTO addressDTO) {
        if (addressDTO.getId() == null) {
            throw new IllegalArgumentException("地址ID不能为空");
        }
        
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        
        ShippingAddress address = shippingAddressRepository.findByIdAndUser(addressDTO.getId(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("收货地址不存在"));
        
        address.setReceiverName(addressDTO.getReceiverName());
        address.setReceiverPhone(addressDTO.getReceiverPhone());
        address.setAddress(addressDTO.getAddress());
        
        // 如果设置为默认地址，需要将其他地址设置为非默认
        if (Boolean.TRUE.equals(addressDTO.getIsDefault()) && !Boolean.TRUE.equals(address.getIsDefault())) {
            shippingAddressRepository.clearDefaultAddress(currentUser.getId());
            address.setIsDefault(true);
        } else {
            address.setIsDefault(addressDTO.getIsDefault());
        }
        
        address.setUpdateTime(LocalDateTime.now());
        return shippingAddressRepository.save(address);
    }
    
    /**
     * 删除收货地址
     * @param id 收货地址ID
     * @return 是否删除成功
     */
    @Override
    @Transactional
    public boolean deleteAddress(Long id) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        
        // 先检查地址是否存在
        ShippingAddress address = shippingAddressRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("收货地址不存在"));
        
        shippingAddressRepository.delete(address);
        return true;
    }
    
    /**
     * 设置默认收货地址
     * @param id 收货地址ID
     * @return 设置为默认的收货地址
     */
    @Override
    @Transactional
    public ShippingAddress setDefaultAddress(Long id) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        
        ShippingAddress address = shippingAddressRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("收货地址不存在"));
        
        // 将其他地址设置为非默认
        shippingAddressRepository.clearDefaultAddress(currentUser.getId());
        
        // 设置当前地址为默认
        address.setIsDefault(true);
        address.setUpdateTime(LocalDateTime.now());
        return shippingAddressRepository.save(address);
    }
} 