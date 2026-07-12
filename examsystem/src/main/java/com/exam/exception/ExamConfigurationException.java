package com.exam.exception;

/**
 * Thrown when an exam cannot be started because it is not properly
 * configured (e.g. no questions added, invalid duration).
 */
public class ExamConfigurationException extends Exception {

    public ExamConfigurationException(String message) {
        super(message);
    }
}
