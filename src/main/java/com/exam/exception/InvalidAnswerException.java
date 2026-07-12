package com.exam.exception;

/**
 * Thrown when a student's submitted answer fails validation
 * (e.g. empty answer, option index out of range, wrong data type).
 */
public class InvalidAnswerException extends Exception {

    public InvalidAnswerException(String message) {
        super(message);
    }

    public InvalidAnswerException(String message, Throwable cause) {
        super(message, cause);
    }
}
