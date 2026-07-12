package com.exam.model;

import com.exam.exception.InvalidAnswerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A question with a fixed set of options where exactly one is correct.
 * Student answers are submitted as the option's index (as a String, e.g. "0").
 */
public class MultipleChoiceQuestion extends Question {

    private final List<String> options;
    private final int correctOptionIndex;

    public MultipleChoiceQuestion(String questionText, int marks, List<String> options, int correctOptionIndex, String category) {
        super(questionText, marks, category);
        if (options == null || options.size() < 2) {
            throw new IllegalArgumentException("A multiple choice question needs at least 2 options.");
        }
        if (correctOptionIndex < 0 || correctOptionIndex >= options.size()) {
            throw new IllegalArgumentException("Correct option index is out of range.");
        }
        this.options = new ArrayList<>(options);
        this.correctOptionIndex = correctOptionIndex;
    }

    /** Convenience constructor that assigns the default category. */
    public MultipleChoiceQuestion(String questionText, int marks, List<String> options, int correctOptionIndex) {
        this(questionText, marks, options, correctOptionIndex, DEFAULT_CATEGORY);
    }

    public List<String> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    @Override
    public boolean isCorrect(String studentAnswer) throws InvalidAnswerException {
        if (studentAnswer == null || studentAnswer.isBlank()) {
            throw new InvalidAnswerException("No option was selected for: \"" + getQuestionText() + "\"");
        }
        int chosen;
        try {
            chosen = Integer.parseInt(studentAnswer.trim());
        } catch (NumberFormatException e) {
            throw new InvalidAnswerException("Answer for \"" + getQuestionText() + "\" is not a valid option.", e);
        }
        if (chosen < 0 || chosen >= options.size()) {
            throw new InvalidAnswerException("Selected option is out of range for: \"" + getQuestionText() + "\"");
        }
        return chosen == correctOptionIndex;
    }

    @Override
    public String getCorrectAnswerDisplay() {
        return options.get(correctOptionIndex);
    }

    @Override
    public String getQuestionType() {
        return "Multiple Choice";
    }
}
