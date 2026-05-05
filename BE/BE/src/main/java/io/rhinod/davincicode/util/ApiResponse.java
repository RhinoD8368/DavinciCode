package io.rhinod.davincicode.util;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor  
@AllArgsConstructor
public class ApiResponse<T> {
	private boolean success;
	private String message;
	private T data;
	
	public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
		return ResponseEntity.ok(new ApiResponse<>(true, "Success", data));
	}
	
	public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message ) {
		return ResponseEntity.ok(new ApiResponse<>(true, message, data));
	}
	
	public static <T> ResponseEntity<ApiResponse<T>> success(HttpHeaders headers, T data, String message) {
		return ResponseEntity.ok()
	            .headers(headers)
	            .body(new ApiResponse<>(true, message, data));
	}
	
	public static ResponseEntity<ApiResponse<Object>> fail(String message) {
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, message, null));
    }
	
	public static ResponseEntity<ApiResponse<Object>> fail(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ApiResponse<>(false, message, null));
    }
	
	public static String convertToJson(boolean success, String message) throws JsonProcessingException {
	    ObjectMapper mapper = new ObjectMapper();
	    return mapper.writeValueAsString(new ApiResponse<>(success, message, null));
	}
}
