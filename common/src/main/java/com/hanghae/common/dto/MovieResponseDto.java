package com.hanghae.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드 초기화 생성자
public class MovieResponseDto {

    private String title;          // 영화 제목
    private String rating;         // 영상물 등급

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate releaseDate; // 개봉일

    private String thumbnailUrl;   // 썸네일 이미지 URL
    private Integer duration;      // 러닝 타임 (분)
    private String genre;          // 영화 장르
    private List<ShowtimeDto> showtimes; // 상영 시간표

    @Data
    @NoArgsConstructor // 기본 생성자
    @AllArgsConstructor
    public static class ShowtimeDto {
        private String theaterName;      // 상영관 이름

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        private LocalDateTime startTime; // 상영 시작 시간

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        private LocalDateTime endTime;   // 상영 종료 시간
    }
}