package com.passtival.backend.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;

@Getter
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class BaseResponse<T> {

	@JsonProperty("isSuccess")
	private final boolean isSuccess;

	@JsonProperty("code")
	private final int code;

	@JsonProperty("message")
	private final String message;

	@JsonProperty("result")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T result;

	// 요청에 성공한 경우
	public BaseResponse(T result) {
		this.isSuccess = BaseResponseStatus.OK.isSuccess();
		this.code = BaseResponseStatus.OK.getCode();
		this.message = BaseResponseStatus.OK.getMessage();
		this.result = result;
	}

	// 요청에 실패한 경우
	public BaseResponse(BaseResponseStatus status) {
		this.isSuccess = status.isSuccess();
		this.code = status.getCode();
		this.message = status.getMessage();
	}

	// 요청에 실패한 경우 + response 담을 값이 있는 경우
	public BaseResponse(BaseResponseStatus status, T result) {
		this.isSuccess = status.isSuccess();
		this.code = status.getCode();
		this.message = status.getMessage();
		this.result =result;
	}

	// 커스텀 메세지 입력
	public BaseResponse(BaseResponseStatus status, String customMessage) {
		this.isSuccess = status.isSuccess();
		this.code = status.getCode();
		this.message = customMessage;
	}
}
