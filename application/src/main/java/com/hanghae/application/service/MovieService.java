package com.hanghae.application.service;

import com.hanghae.common.dto.MovieRequestDto;
import com.hanghae.common.dto.MovieResponseDto;

import com.hanghae.domain.entity.Movie;
import com.hanghae.domain.entity.QGenre;
import com.hanghae.domain.entity.QMovie;
import com.hanghae.domain.entity.QShowtime;
import com.hanghae.domain.repository.MovieRepository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final RedisService redisService;
    private final JPAQueryFactory jpaQueryFactory;

    public MovieService(MovieRepository movieRepository, RedisService redisService,JPAQueryFactory jpaQueryFactory) {
        this.movieRepository = movieRepository;
        this.redisService = redisService;
        this.jpaQueryFactory = jpaQueryFactory;
    }

    /**
     * 캐시를 사용하여 QueryDSL로 영화 정보를 조회
     * 1. 짧은 검색어 제외: title 길이가 3자 미만이면 캐싱하지 않고 실시간 조회.
     *    - 이유: 짧은 검색어는 결과 범위가 넓고 캐시 키가 과도하게 생성될 가능성.
     * 2. 캐시 키 생성: title과 genre를 조합하여 고유 키 생성.
     *    - 전체 데이터(조건 없음): "movies:all" 키로 캐싱.
     *    - 특정 장르만: "movies:genre={genre}:title=all" 형식으로 키 생성.
     *    - 검색 결과: "movies:search:title={title}:genre={genre}" 형식으로 키 생성.
     * 3. TTL 설정:
     *    - 전체 데이터 또는 특정 장르: 10분 유지 (변경 가능성이 적음).
     *    - 검색 결과 데이터: 5분 유지 (사용자 요청 빈도가 높음).
     * 4. 인기 검색어 관리: title 조회 횟수가 일정 기준(예: 100회) 이상이면 "popular" 키로 별도 캐싱.
     *    - "movies:popular:title={title}" 형식으로 키 생성.
     */
    public List<MovieResponseDto> getMoviesWithCache(MovieRequestDto requestDto) {
        String title = requestDto.getTitle();
        String genre = requestDto.getGenre();

        // 너무 짧은 검색어는 캐싱하지 않고 바로 조회
        if (title != null && title.length() < 3) {
            return fetchMovies(title, genre);
        }

        // 캐시 키 생성
        String cacheKey;
        if (title == null && genre == null) {
            cacheKey = "movies:all"; // 전체 데이터 캐시 키
        } else if (title == null) {
            cacheKey = "movies:genre=" + genre + ":title=all"; // 특정 장르만 캐싱
        } else {
            cacheKey = "movies:search:title=" + title + ":genre=" + (genre != null ? genre : "all"); // 검색 결과 캐싱
        }

        // Redis 캐시에서 데이터 조회
        Object cachedMovies = redisService.get(cacheKey);
        if (cachedMovies != null) {
            // 캐시 데이터가 존재하면 반환
            return (List<MovieResponseDto>) cachedMovies;
        }

        // 캐시에 데이터가 없으면 데이터 조회
        List<MovieResponseDto> movies = fetchMovies(title, genre);

        // Redis에 저장 (TTL: 전체 데이터 10분, 검색 결과 5분)
        int ttl = (title == null) ? 10 : 5; // title이 없는 경우는 메인 화면 데이터
        redisService.set(cacheKey, movies, ttl, TimeUnit.MINUTES);

        return movies;
    }


    /**
     * QueryDSL 로 영화 조회
     */
    private List<MovieResponseDto> fetchMovies(String title, String genre) {
        // QueryDSL을 사용하여 동적 쿼리 생성
        // 현재 날짜를 기준으로 상영 중인 영화 조회
        LocalDate today = LocalDate.now();
        QMovie qMovie = QMovie.movie;
        QShowtime qShowtime = QShowtime.showtime;
/*
        // [2주차] - QueryDSL - entity 기반 Projection (비효율적)
        List<Movie> movies = jpaQueryFactory.selectFrom(qMovie)
                .where(
                        title != null ? qMovie.title.containsIgnoreCase(title) : null,
                        genre != null ? qMovie.genre.genreName.equalsIgnoreCase(genre) : null,
                        qMovie.releaseDate.loe(today) // 개봉일이 오늘 이전 또는 오늘인 경우
                )
                .fetch();
        return convertToDto(movies);
*/
        // [2주차] - QueryDSL - DTO 기반 Projection (필요한 필드만 조회, 매핑)
        List<MovieResponseDto> movies = jpaQueryFactory.select(
                        Projections.fields(MovieResponseDto.class,
                                qMovie.title.as("title"),
                                qMovie.rating.as("rating"),
                                qMovie.releaseDate.as("releaseDate"),
                                qMovie.thumbnailUrl.as("thumbnailUrl"),
                                qMovie.duration.as("duration"),
                                qMovie.genre.genreName.as("genre"),
                                qShowtime.theater.theaterName.as("theaterName"),
                                qShowtime.startTime.as("startTime"),
                                qShowtime.endTime.as("endTime")
                        )
                )
                .from(qMovie)
                .leftJoin(qMovie.showtimes, qShowtime)
                .where(
                        title != null ? qMovie.title.containsIgnoreCase(title) : null,
                        genre != null ? qMovie.genre.genreName.equalsIgnoreCase(genre) : null,
                        qMovie.releaseDate.loe(today)
                )
                .fetch();
        return movies;
    }

    /**
     * JPQL로 영화 정보를 조회
     */
    public List<MovieResponseDto> getMoviesUsingJPQL() {
        // JPQL 쿼리 메서드를 사용하여 영화 조회
        List<Movie> movies = movieRepository.findMoviesWithDetails();
        return convertToDto(movies);
    }

    /**
     * 공통적으로 Movie 엔티티를 MovieResponseDto로 변환
     */
    private List<MovieResponseDto> convertToDto(List<Movie> movies) {
        return movies.stream()
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
    }

}