package org.simple.booking.service.domain.exception;

public class BookingBadRequestException extends RuntimeException {

    public BookingBadRequestException(final String exceptionMsg) {
        super(exceptionMsg);
    }

}
