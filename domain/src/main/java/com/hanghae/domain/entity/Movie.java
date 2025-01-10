package com.hanghae.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "movie")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Integer id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 50)
    private String rating;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "thumbnail_url", length = 255)
    private String thumbnailUrl;

//    @ManyToOne
//    @JoinColumn(name = "genre_id", nullable = false)
//    private Genre genre;

    private Integer duration;

    @Column(name = "created_by", length = 20)
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_by", length = 20)
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 기본 생성자
    protected Movie() {}

    // 생성자, Getter, Setter 생략
}
