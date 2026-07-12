package com.exam.model;

import com.exam.exception.InvalidAnswerException;

/**
 * A free-text question graded by comparing the student's answer against a
 * model answer, ignoring case and surrounding whitespace.
 */
public class ShortAnswerQuestion extends Question {

    private final String modelAnswer;

    public ShortAnswerQuestion(String questionText, int marks, String modelAnswer, String category) {
        super(questionText, marks, category);
        if (modelAnswer == null || modelAnswer.isBlank()) {
            throw new IllegalArgumentException("Model answer cannot be empty.");
        }
        this.modelAnswer = modelAnswer.trim();
    }

    /** Convenience constructor that assigns the default category. */
    public ShortAnswerQuestion(String questionText, int marks, String modelAnswer) {
        this(questionText, marks, modelAnswer, DEFAULT_CATEGORY);
    }

    public String getModelAnswer() {
        return modelAnswer;
    }

    @Override
    public boolean isCorrect(String studentAnswer) throws InvalidAnswerException {
        if (studentAnswer == null || studentAnswer.isBlank()) {
            throw new InvalidAnswerException("No answer was written for: \"" + getQuestionText() + "\"");
        }
        return studentAnswer.trim().equalsIgnoreCase(modelAnswer);
    }

    @Override
    public String getCorrectAnswerDisplay() {
        return modelAnswer;
    }

    @Override
    public String getQuestionType() {
        return "Short Answer";
    }
}
