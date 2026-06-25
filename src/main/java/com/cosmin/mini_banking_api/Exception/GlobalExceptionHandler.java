package com.cosmin.mini_banking_api.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotAdminException.class)
    public ResponseEntity<Map<String,String >> accessDeniedHandler(NotAdminException e){
        Map<String,String> response = new HashMap<>();

        response.put("error" , "Forbidden");
        response.put("message" , e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);

    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String,String >> accountNotFoundHandler(AccountNotFoundException e){
        Map<String,String> response = new HashMap<>();

        response.put("error" , "Not found");
        response.put("message" , e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler(CantChangeOwnRoleException.class)
    public ResponseEntity<Map<String,String >> cantChangeOwnRoleHandler(CantChangeOwnRoleException e){
        Map<String,String> response = new HashMap<>();

        response.put("error" , "Forbidden");
        response.put("message" , e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);

    }
    @ExceptionHandler(CantTransferToOwnAccountException.class)
    public ResponseEntity<Map<String,String >> cantTransferToOwnAccountHandler(CantTransferToOwnAccountException e){
        Map<String,String> response = new HashMap<>();

        response.put("error" , "Bad request");
        response.put("message" , e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Map<String,String >> insufficientFundsHandler(InsufficientFundsException e){
        Map<String,String> response = new HashMap<>();

        response.put("error" , "Bad request");
        response.put("message" , e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String,String >> emailAlreadyExistsHandler(EmailAlreadyExistsException e){
        Map<String,String> response = new HashMap<>();

        response.put("error" , "Conflict");
        response.put("message" , e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);

    }
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String,String >> invalidCredentialsHandler(InvalidCredentialsException e){
        Map<String,String> response = new HashMap<>();

        response.put("error" , "Unauthorized");
        response.put("message" , e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);

    }
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Map<String,String >> usernameAlreadyExistsHandler(UsernameAlreadyExistsException e){
        Map<String,String> response = new HashMap<>();

        response.put("error" , "Conflict");
        response.put("message" , e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);

    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String,String >> userNotFoundHandler(UserNotFoundException e){
        Map<String,String> response = new HashMap<>();

        response.put("error" , "Not found");
        response.put("message" , e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> exceptionHandle(Exception e){
        Map<String ,String > response = new HashMap<>();
        response.put("error", "Internal server error");
        response.put("message" , "Something went wrong");

        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> validException(MethodArgumentNotValidException exc){
        Map<String,String> response = new HashMap<>();

        for (FieldError fieldError : exc.getBindingResult().getFieldErrors()) {
            response.put(fieldError.getField() , fieldError.getDefaultMessage());
        }

        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

}
