package com.hanghae.application.service;


import com.hanghae.common.dto.MovieResponseDto;
import com.hanghae.domain.repository.MovieRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final RedisService redisService;

    public MovieService(MovieRepository movieRepository, RedisService redisService) {
        this.movieRepository = movieRepository;
        this.redisService = redisService;
    }

    public List<MovieResponseDto> getMovies() {
        String cacheKey = "movies"; // 캐시 키 설정

        // Redis 캐시에서 데이터 조회
        Object cachedMovies = redisService.get(cacheKey);
        if (cachedMovies != null) {
            // 캐시 데이터가 존재하면 반환
            return (List<MovieResponseDto>) cachedMovies;
        }

        // 캐시에 데이터가 없으면 데이터베이스에서 조회
        List<MovieResponseDto> movies = movieRepository.findMoviesWithDetails().stream()
                .map(movie -> new MovieResponseDto(
                        movie.getTitle(),
                        movie.getRating(),
                        movie.getReleaseDate(),
                        movie.getThumbnailUrl(),
                        movie.getDuration(),
                        movie.getGenre().getGenreName(),
                        movie.getShowtimes().stream()
                                .map(showtime -> new MovieResponseDto.ShowtimeDto(
                                        showtime.getTheater().getTheaterName(),
                                        showtime.getStartTime(),
                                        showtime.getEndTime()
                                ))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        // 데이터베이스에서 조회한 결과를 Redis에 저장 (유효 시간 10분)
        redisService.set(cacheKey, movies, 10, TimeUnit.MINUTES);

        return movies;
    }
}