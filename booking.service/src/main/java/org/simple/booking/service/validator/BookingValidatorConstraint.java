package org.simple.booking.service.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.simple.booking.service.domain.Booking;

public class BookingValidatorConstraint implements ConstraintValidator<BookingValidationConstraint, Booking> {

    private final String START_TIME_CANNOT_BE_AFTER_END_TIME = "startTime cannot be after endTime. ";
    private final String START_TIME = "startTime: ";
    private final String END_TIME = " endTime: ";

    @Override
    public void initialize(final BookingValidationConstraint bookingValidationConstraint) {
    }

    @Override
    public boolean isValid(Booking booking, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        if (booking.getStart().isAfter(booking.getEnd())) {
            addCustomExceptionMsg(context, START_TIME_CANNOT_BE_AFTER_END_TIME + START_TIME + booking.getStart() + END_TIME + booking.getEnd());

            return false;
        }

        return true;
    }

    private void addCustomExceptionMsg(final ConstraintValidatorContext context, final String message) {
        context.buildConstraintViolationWithTemplate(message)
               .addConstraintViolation();
    }

}