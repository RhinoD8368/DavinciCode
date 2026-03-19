package io.rhinod.davincicode.apiTest.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.rhinod.davincicode.apiTest.service.ApiTestService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ApiTestController {
	
	private final ApiTestService apiTestService;
	
	@GetMapping("/test") 
	public List<Map<String, Object>> getAllUser() throws Exception {
		
		System.out.println("########### test" + apiTestService.getAllUser());
		
		
		
		return apiTestService.getAllUser();
	}
}
