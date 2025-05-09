package com.thfh.service;

import com.thfh.dto.ShippingAddressDTO;
import com.thfh.model.ShippingAddress;

import java.util.List;

/**
 * 收货地址服务接口
 */
public interface ShippingAddressService {
    
    /**
     * 获取当前用户的所有收货地址
     * @return 收货地址列表
     */
    List<ShippingAddress> getCurrentUserAddresses();
    
    /**
     * 根据ID获取收货地址
     * @param id 收货地址ID
     * @return 收货地址
     */
    ShippingAddress getAddressById(Long id);
    
    /**
     * 获取当前用户的默认收货地址
     * @return 默认收货地址，如果不存在则返回null
     */
    ShippingAddress getDefaultAddress();
    
    /**
     * 添加收货地址
     * @param addressDTO 收货地址DTO
     * @return 添加后的收货地址
     */
    ShippingAddress addAddress(ShippingAddressDTO addressDTO);
    
    /**
     * 更新收货地址
     * @param addressDTO 收货地址DTO
     * @return 更新后的收货地址
     */
    ShippingAddress updateAddress(ShippingAddressDTO addressDTO);
    
    /**
     * 删除收货地址
     * @param id 收货地址ID
     * @return 是否删除成功
     */
    boolean deleteAddress(Long id);
    
    /**
     * 设置默认收货地址
     * @param id 收货地址ID
     * @return 设置为默认的收货地址
     */
    ShippingAddress setDefaultAddress(Long id);
} 