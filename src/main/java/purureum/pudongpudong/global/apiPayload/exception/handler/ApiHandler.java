package purureum.pudongpudong.global.apiPayload.exception.handler;

import purureum.pudongpudong.global.apiPayload.code.BaseErrorCode;
import purureum.pudongpudong.global.apiPayload.exception.GeneralException;

public class ApiHandler extends GeneralException {
	
	public ApiHandler(BaseErrorCode errorCode) {
		super(errorCode);
	}
}
