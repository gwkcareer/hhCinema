package com.hanghae.application.service;


import com.hanghae.domain.entity.Movie;
import com.hanghae.domain.repository.MovieRepository;
import com.hanghae.application.service.exception.MovieNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // 모든 영화 조회
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    // ID로 영화 조회
    public Movie getMovieById(Integer id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));
    }

    // 새로운 영화 추가
    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }

//    // 기존 영화 정보 수정
//    public Movie updateMovie(Integer id, Movie movieDetails) {
//        Movie existingMovie = getMovieById(id);
//        existingMovie.setTitle(movieDetails.getTitle());
//        existingMovie.setRating(movieDetails.getRating());
//        existingMovie.setReleaseDate(movieDetails.getReleaseDate());
//        existingMovie.setThumbnailUrl(movieDetails.getThumbnailUrl());
//        existingMovie.setDuration(movieDetails.getDuration());
//        return movieRepository.save(existingMovie);
//    }

    // 영화 삭제
    public void deleteMovie(Integer id) {
        Movie existingMovie = getMovieById(id);
        movieRepository.delete(existingMovie);
    }
}