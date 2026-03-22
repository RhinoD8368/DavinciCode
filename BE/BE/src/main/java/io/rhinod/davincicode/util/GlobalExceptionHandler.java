package io.rhinod.davincicode.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	
//	@ExceptionHandler(org.springframework.dao.DuplicateKeyException.class)
	
	@ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        log.error("런타임 에러 발생: {}", e.getMessage());
        return ApiResponse.fail(e.getMessage());
    }
	
	@ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllException(Exception e) {
        // 여기서 e.printStackTrace()를 찍어줘야 서버 콘솔에서 어디가 틀렸는지 확인 가능!
        log.error("예상치 못한 에러 발생!", e); 
        return ApiResponse.fail("서버 내부 오류가 발생했습니다. 관리자에게 문의하세요.");
    }
}
