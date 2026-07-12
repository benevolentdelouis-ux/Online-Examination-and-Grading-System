package com.exam.model;

import com.exam.exception.InvalidAnswerException;

/**
 * A simple True/False question. Student answers are submitted as the
 * literal String "true" or "false" (case-insensitive).
 */
public class TrueFalseQuestion extends Question {

    private final boolean correctAnswer;

    public TrueFalseQuestion(String questionText, int marks, boolean correctAnswer, String category) {
        super(questionText, marks, category);
        this.correctAnswer = correctAnswer;
    }

    /** Convenience constructor that assigns the default category. */
    public TrueFalseQuestion(String questionText, int marks, boolean correctAnswer) {
        this(questionText, marks, correctAnswer, DEFAULT_CATEGORY);
    }

    public boolean getCorrectAnswer() {
        return correctAnswer;
    }

    @Override
    public boolean isCorrect(String studentAnswer) throws InvalidAnswerException {
        if (studentAnswer == null || studentAnswer.isBlank()) {
            throw new InvalidAnswerException("No answer was selected for: \"" + getQuestionText() + "\"");
        }
        String normalized = studentAnswer.trim().toLowerCase();
        if (!normalized.equals("true") && !normalized.equals("false")) {
            throw new InvalidAnswerException("Answer for \"" + getQuestionText() + "\" must be True or False.");
        }
        return Boolean.parseBoolean(normalized) == correctAnswer;
    }

    @Override
    public String getCorrectAnswerDisplay() {
        return correctAnswer ? "True" : "False";
    }

    @Override
    public String getQuestionType() {
        return "True / False";
    }
}
