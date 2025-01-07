package com.example.vinylstore.SERVICES;

import com.example.vinylstore.MODELS.User;
import com.example.vinylstore.MODELS.Vinyl;
import com.example.vinylstore.REPOSITORIES.VinylRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VinylService {

    @Autowired
    private VinylRepository vinylRepository;

    public List<Vinyl> getAllVinyls() {
        return vinylRepository.findAll();
    }

    public void increaseStock(Long vinylId, int quantity) {
        Optional<Vinyl> vinylOptional = vinylRepository.findById(vinylId);
        if (vinylOptional.isEmpty()) {
            throw new RuntimeException("Vinyl not found");
        }
        vinylRepository.changeStock(vinylId, quantity);
    }


    public Vinyl getVinylById(Long vinylId) {
        return vinylRepository.findById(vinylId)
                .orElseThrow(() -> new IllegalArgumentException("Vinyl not found with id: " + vinylId));
    }
    public Vinyl addVinyl(Vinyl vinyl) {
        return vinylRepository.save(vinyl);
    }

    @Transactional
    public void removeVinyl(Long vinylId) {
        Vinyl vinyl = vinylRepository.findById(vinylId)
                .orElseThrow(() -> new IllegalArgumentException("Vinyl with ID " + vinylId + " does not exist"));

        vinyl.getOrderItems().forEach(orderItem -> {
            orderItem.getOrder().removeOrderItem(orderItem);
        });

        vinylRepository.delete(vinyl);
    }

}
