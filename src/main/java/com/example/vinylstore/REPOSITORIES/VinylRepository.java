package com.example.vinylstore.REPOSITORIES;

import com.example.vinylstore.MODELS.Vinyl;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VinylRepository extends JpaRepository<Vinyl,Integer> {
    List<Vinyl> findAll();
    Optional<Vinyl> findById(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Vinyl v SET v.stock = v.stock + :quantity WHERE v.id = :id")
    void changeStock(@Param("id") Long id, @Param("quantity") int quantity);

    void deleteById(Long vinylId);
}
