package com.hanghae.adapter.controller;

import com.hanghae.application.service.MovieService;
import com.hanghae.domain.entity.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // 모든 영화 조회
    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        return ResponseEntity.ok(movies);
    }

    // ID로 특정 영화 조회
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Integer id) {
        Movie movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }

    // 새로운 영화 추가
    @PostMapping
    public ResponseEntity<Movie> createMovie(@RequestBody Movie movie) {
        Movie createdMovie = movieService.createMovie(movie);
        return ResponseEntity.status(201).body(createdMovie);
    }

//    // 기존 영화 정보 수정
//    @PutMapping("/{id}")
//    public ResponseEntity<Movie> updateMovie(@PathVariable Integer id, @RequestBody Movie movieDetails) {
//        Movie updatedMovie = movieService.updateMovie(id, movieDetails);
//        return ResponseEntity.ok(updatedMovie);
//    }

    // 영화 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Integer id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}