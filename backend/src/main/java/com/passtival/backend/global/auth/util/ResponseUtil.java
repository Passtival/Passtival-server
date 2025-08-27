package com.passtival.backend.global.auth.util;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ResponseUtil {

	private final ObjectMapper objectMapper;

	public void sendErrorResponse(HttpServletResponse response, BaseResponseStatus status) throws IOException {
		response.setStatus(status.getCode());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		BaseResponse<Object> errorResponse = BaseResponse.fail(status);
		String jsonResponse = objectMapper.writeValueAsString(errorResponse);

		response.getWriter().write(jsonResponse);
	}
}