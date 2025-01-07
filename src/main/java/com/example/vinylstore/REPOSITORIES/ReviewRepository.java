package com.example.vinylstore.REPOSITORIES;

import com.example.vinylstore.MODELS.Review;
import com.example.vinylstore.MODELS.Vinyl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Integer> {
    List<Review> findAll();
    Review findById(Long id);
}
