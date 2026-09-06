    package com.anikesh.saas_backend.Exception;

    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.ExceptionHandler;
    import org.springframework.web.bind.annotation.RestControllerAdvice;

    @RestControllerAdvice
    public class GlobalExceptionHandler {
        
        @ExceptionHandler(CustomException.class)
        public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex){
        
            ErrorResponse errorResponse =
                    new ErrorResponse(ex.getMessage(), ex.getStatus());

                    return ResponseEntity
                    .status(ex.getStatus())
                    .body(errorResponse);
        }
        }

