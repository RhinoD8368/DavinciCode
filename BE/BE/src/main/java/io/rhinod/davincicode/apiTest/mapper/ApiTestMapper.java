package io.rhinod.davincicode.apiTest.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApiTestMapper {

	List<Map<String, Object>> getAllUser() throws Exception;
	
}
