package com.example.vinylstore.REPOSITORIES;

import com.example.vinylstore.MODELS.ShippingAddress;
import com.example.vinylstore.MODELS.Vinyl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress,Integer> {
    List<ShippingAddress> findAll();
    ShippingAddress findById(Long id);
}
