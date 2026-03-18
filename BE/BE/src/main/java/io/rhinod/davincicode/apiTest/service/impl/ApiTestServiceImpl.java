package io.rhinod.davincicode.apiTest.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.rhinod.davincicode.apiTest.mapper.ApiTestMapper;
import io.rhinod.davincicode.apiTest.service.ApiTestService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiTestServiceImpl implements ApiTestService {
	
	private final ApiTestMapper apiTestMapper;
	
	@Override
	public List<Map<String, Object>> getAllUser() throws Exception {
		return apiTestMapper.getAllUser();
	}

}
