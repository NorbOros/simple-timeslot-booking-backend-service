package org.simple.booking.service.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.simple.booking.service.validator.BookingValidationConstraint;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@Jacksonized
@BookingValidationConstraint
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Booking {

    // Usually the id coming from the underlining repository layer
    private final String id;

    @Setter
    @NotNull(message = "Client cannot be null.")
    private String client;

    @NotNull(message = "Start date and time cannot be null.")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private final LocalDateTime start;

    @NotNull(message = "End date and time cannot be null.")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private final LocalDateTime end;



}
