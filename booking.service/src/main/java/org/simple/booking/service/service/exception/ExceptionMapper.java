package org.simple.booking.service.service.exception;

import org.simple.booking.service.domain.exception.BookingBadRequestException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ExceptionMapper {

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BookingBadRequestException.class)
    public ResponseEntity<Object> mapBadRequestException(final BookingBadRequestException exception) {
        return ResponseEntity.badRequest()
                             .body(exception.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> mapMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest()
                             .body(extractExceptionMsgList(exception));
    }

    private List<String> extractExceptionMsgList(final MethodArgumentNotValidException exception) {
        return exception.getBindingResult().getAllErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .toList();
    }
}
