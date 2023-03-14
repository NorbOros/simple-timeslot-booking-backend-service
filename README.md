# Simple time slot booking backend service
Spring boot application implemented for booking timeslots for appointments.

## Tech stack
* SpringBoot 3.0.4
* Java 17
* Lombok

## Configuration 
For configuration please see the application.yml config file.

## Rest
* Listening port: 8080
* Context base path: booking-backend-service

### Endpoints 

### @POST ../v1/book
Used for register new booking.
Accepted object
```
{
  "start":"yyyy-MM-dd HH:mm",
  "end":"yyyy-MM-dd HH:mm",
  "client":"test user"
}
```
### @GET ../v1/booked
Used for fetching all booked timeslots

### @GET ../v1/booked/timeframe
Used for fetching all booked timeslots in a timeframe
Accepted RequestParams:
```
* start: "yyyy-MM-dd HH:mm"
* end: "yyyy-MM-dd HH:mm" 
```

### @GET ../v1/free
Used for fetching all the free time slots 

### @GET ../v1/free/timeframe
Used for fetching all free timeslots in a timeframe
Accepted RequestParams:
```
* start: "yyyy-MM-dd HH:mm"
* end: "yyyy-MM-dd HH:mm" 
```

### @GET ../v1/specific-time/{specifiedTime}
Used for finding the status of given timeslot, by the specified time.
Accepted PathVariable:
```
* specifiedTime: "yyyy-MM-dd HH:mm"
```

### @GET ../v1/client/{client}
Used for fetching all the booked timeslots by the given user.
Accepted PathVariable:
```
* client: test_user
```
