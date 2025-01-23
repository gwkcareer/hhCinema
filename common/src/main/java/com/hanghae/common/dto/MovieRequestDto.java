package com.hanghae.common.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequestDto {
    @Size(max = 255, message = "영화 제목은 255자 이내여야 합니다.")
    private String title;

    private String genre;
}