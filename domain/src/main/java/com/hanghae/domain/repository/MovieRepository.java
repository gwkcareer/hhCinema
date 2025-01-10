package com.hanghae.domain.repository;

import com.hanghae.domain.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
    // 추가적인 쿼리 메서드 정의 가능
}