package com.pd.gilgeorigoreuda.image.exception;

import com.pd.gilgeorigoreuda.common.exception.GilgeorigoreudaException;
import org.springframework.http.HttpStatus;

public class ImageNullException extends GilgeorigoreudaException {
    public ImageNullException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
