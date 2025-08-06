package com.ayman.paymentservice.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }

}
