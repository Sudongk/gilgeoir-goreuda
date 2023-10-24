package com.pd.gilgeorigoreuda.store.exception;

import org.springframework.http.HttpStatus;

import com.pd.gilgeorigoreuda.common.exception.GilgeorigoreudaException;

public class NoSuchFoodTypeException extends GilgeorigoreudaException {
	public NoSuchFoodTypeException() {
		super(HttpStatus.BAD_REQUEST);
	}
}
