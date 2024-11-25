package com.moderathowinther.usermanagement.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.Set;

public class InvalidUserInputException extends ConstraintViolationException {

    public InvalidUserInputException(String msg, Set<? extends ConstraintViolation<?>> bindingResult) {
        super(msg, bindingResult);
    }
}
