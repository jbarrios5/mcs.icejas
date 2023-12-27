package py.com.jmbr.mcs.icejas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import py.com.jmbr.java.commons.exception.ExceptionResponse;
import py.com.jmbr.java.commons.exception.JMBRException;

import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class TransactionException {
    @ExceptionHandler( value = {JMBRException.class, ValidationException.class})
    public ResponseEntity<ExceptionResponse> handleException(JMBRException e){
        return new ResponseEntity<ExceptionResponse>(new ExceptionResponse(e.getMessage(),e.getType()),e.getCode());
    }
    @ExceptionHandler( value = { BindException.class})
    public ResponseEntity<List> handleExceptionBind(BindException bindingResult){
        List<FieldError> errors = bindingResult.getFieldErrors();
        List<String> errorsMessage = new ArrayList<>();
        // Construye un mensaje de error personalizado

        for (FieldError error : errors) {
           errorsMessage.add(error.getDefaultMessage());
        }
        return new ResponseEntity<>(errorsMessage, HttpStatus.BAD_REQUEST);
    }

}
