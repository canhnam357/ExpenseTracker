package xyz.erotskoob.expensetracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceExistedException extends RuntimeException {
    public ResourceExistedException(String message) {
        super(message);
    }
}