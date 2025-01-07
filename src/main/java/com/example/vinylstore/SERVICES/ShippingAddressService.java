package com.example.vinylstore.SERVICES;

import com.example.vinylstore.MODELS.ShippingAddress;
import com.example.vinylstore.MODELS.User;
import com.example.vinylstore.REPOSITORIES.ShippingAddressRepository;
import com.example.vinylstore.REPOSITORIES.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ShippingAddressService {

    private final ShippingAddressRepository addressRepository;

    @Autowired
    public ShippingAddressService(ShippingAddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public void saveAddress(ShippingAddress address) {
        addressRepository.save(address);
    }

}
