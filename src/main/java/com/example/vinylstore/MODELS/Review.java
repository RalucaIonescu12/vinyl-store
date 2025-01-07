package com.example.vinylstore.MODELS;

import jakarta.persistence.*;


@Entity
@Table(name = "reviews")  // Table annotation to specify table name
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  // Many reviews can belong to one vinyl
    @JoinColumn(name = "vinyl_id")
    private Vinyl vinyl;
    @ManyToOne(fetch = FetchType.LAZY)  // Many reviews can belong to one user
    @JoinColumn(name = "user_id")
    private User user;

    private int rating;
    private String comment;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vinyl getVinyl() {
        return vinyl;
    }

    public void setVinyl(Vinyl vinyl) {
        this.vinyl = vinyl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
