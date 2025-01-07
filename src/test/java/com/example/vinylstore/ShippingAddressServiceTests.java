package com.example.vinylstore;

import com.example.vinylstore.MODELS.ShippingAddress;
import com.example.vinylstore.REPOSITORIES.ShippingAddressRepository;
import com.example.vinylstore.SERVICES.ShippingAddressService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippingAddressServiceTests {

    @Mock
    private ShippingAddressRepository addressRepository;

    @InjectMocks
    private ShippingAddressService shippingAddressService;

    @Test
    void saveAddress_Success() {
        ShippingAddress address = new ShippingAddress();

        shippingAddressService.saveAddress(address);

        verify(addressRepository, times(1)).save(address);
    }
}