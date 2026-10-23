package com.whitepencil.guild.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ApiException extends RuntimeException {
    private final int status;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}