package com.example.vinylstore.REPOSITORIES;

import com.example.vinylstore.MODELS.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    User findByUsernameAndPassword(String email, String password);
    User findByUsername(String email);
}
