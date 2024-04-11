package com.pd.gilgeorigoreuda.store.exception;

import com.pd.gilgeorigoreuda.common.exception.GilgeoriGoreudaException;
import org.springframework.http.HttpStatus;

public class NoReporterMemberException extends GilgeoriGoreudaException {
	public NoReporterMemberException() {
		super(HttpStatus.UNAUTHORIZED);
	}
}
