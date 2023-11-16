package com.pd.gilgeorigoreuda.login.exception;

import com.pd.gilgeorigoreuda.common.exception.GilgeorigoreudaException;
import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends GilgeorigoreudaException {
    public InvalidRefreshTokenException() {
        super(HttpStatus.UNAUTHORIZED);
    }
}
