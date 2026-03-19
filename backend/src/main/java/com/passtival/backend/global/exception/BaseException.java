package com.passtival.backend.global.exception;

import com.passtival.backend.global.exception.code.ErrorCode;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

	private final ErrorCode status;

	public BaseException(ErrorCode status) {
		this(status, validateStatus(status).getMessage(), null);
	}

	public BaseException(ErrorCode status, String message) {
		this(status, message, null);
	}

	public BaseException(ErrorCode status, Throwable cause) {
		this(status, validateStatus(status).getMessage(), cause);
	}

	public BaseException(ErrorCode status, String message, Throwable cause) {
		super(message, cause);
		this.status = validateStatus(status);
	}

	private static ErrorCode validateStatus(ErrorCode status) {
		if (status == null) {
			throw new IllegalArgumentException("status는 null일 수 없습니다.");
		}
		return status;
	}
}
