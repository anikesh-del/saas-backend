package com.anikesh.saas_backend.Exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException{
    private final HttpStatus status;
    private final String message;

    public CustomException(String message,HttpStatus status){
        this.message=message;
        this.status=status;
    }

    public HttpStatus getStatus(){
        return status;
    }

    public String getmessage(){
        return message;
    }
}
