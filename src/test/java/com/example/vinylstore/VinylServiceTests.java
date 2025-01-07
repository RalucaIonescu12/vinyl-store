package com.example.vinylstore;

import com.example.vinylstore.MODELS.Vinyl;
import com.example.vinylstore.REPOSITORIES.VinylRepository;
import com.example.vinylstore.SERVICES.VinylService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VinylServiceTests {

    @Mock
    private VinylRepository vinylRepository;

    @InjectMocks
    private VinylService vinylService;

    @Test
    void getAllVinyls_ReturnsVinylList() {
        List<Vinyl> vinylList = Arrays.asList(new Vinyl(), new Vinyl());
        when(vinylRepository.findAll()).thenReturn(vinylList);

        List<Vinyl> result = vinylService.getAllVinyls();

        assertEquals(2, result.size());
        verify(vinylRepository, times(1)).findAll();
    }

    @Test
    void increaseStock_VinylExists() {
        Vinyl vinyl = new Vinyl();
        vinyl.setId(1L);
        vinyl.setStock(10);

        when(vinylRepository.findById(1L)).thenReturn(Optional.of(vinyl));

        vinylService.increaseStock(1L, 5);

        verify(vinylRepository, times(1)).changeStock(1L, 5);
    }

    @Test
    void increaseStock_VinylNotFound() {
        when(vinylRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                vinylService.increaseStock(1L, 5));
    }
}