package hhCinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
		scanBasePackages = {
				"com.hanghae.adapter",       // 어댑터 계층: 컨트롤러 및 API 관련 패키지
				"com.hanghae.application",   // 애플리케이션 계층: 서비스 및 비즈니스 로직 패키지
				"com.hanghae.domain",        // 도메인 계층: 엔티티 및 리포지토리 패키지
				"com.hanghae.infrastructure",// 인프라 계층: 외부 시스템 연동 및 데이터 접근 패키지
				"com.hanghae.common"         // 공통 모듈: DTO, 유틸리티, 공용 코드 패키지
		}
)
@EntityScan(basePackages = "com.hanghae.domain.entity")
public class HhCinemaApplication {

	public static void main(String[] args) {
		SpringApplication.run(HhCinemaApplication.class, args);
	}

}
