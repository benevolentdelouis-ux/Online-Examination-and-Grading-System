package com.exam.model;

import com.exam.exception.InvalidAnswerException;

/**
 * Abstract representation of an exam question.
 *
 * <p>This class captures everything that is common to every question type
 * (an id, the question text and how many marks it is worth) while leaving
 * the type-specific behaviour - how an answer is validated, how it is
 * graded and how the correct answer is displayed - to concrete subclasses.
 * This is the project's core example of <b>abstraction</b>: client code
 * (the UI, the grading service) only ever talks to a {@code Question},
 * never to a specific subtype, yet each subtype behaves differently
 * through <b>polymorphism</b>.</p>
 */
public abstract class Question {

    /** Simple incrementing id generator shared by every question created. */
    private static int nextId = 1;

    public static final String DEFAULT_CATEGORY = "General";

    private final int id;
    private String questionText;
    private int marks;
    private String category;

    protected Question(String questionText, int marks, String category) {
        if (questionText == null || questionText.isBlank()) {
            throw new IllegalArgumentException("Question text cannot be empty.");
        }
        if (marks <= 0) {
            throw new IllegalArgumentException("Marks must be a positive number.");
        }
        this.id = nextId++;
        this.questionText = questionText.trim();
        this.marks = marks;
        setCategory(category);
    }

    /** Convenience constructor for callers that don't need a specific category. */
    protected Question(String questionText, int marks) {
        this(questionText, marks, DEFAULT_CATEGORY);
    }

    // ----- Encapsulated accessors -----------------------------------------

    public int getId() {
        return id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        if (questionText == null || questionText.isBlank()) {
            throw new IllegalArgumentException("Question text cannot be empty.");
        }
        this.questionText = questionText.trim();
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        if (marks <= 0) {
            throw new IllegalArgumentException("Marks must be a positive number.");
        }
        this.marks = marks;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = (category == null || category.isBlank()) ? DEFAULT_CATEGORY : category.trim();
    }

    // ----- Behaviour every subclass must supply (abstraction) -------------

    /**
     * Checks whether the given raw student answer is correct.
     *
     * @throws InvalidAnswerException if the answer is missing or is not in
     *                                a format this question type can grade
     */
    public abstract boolean isCorrect(String studentAnswer) throws InvalidAnswerException;

    /** Human-readable form of the correct answer, used on the results screen. */
    public abstract String getCorrectAnswerDisplay();

    /** Short label identifying the concrete question type, e.g. "Multiple Choice". */
    public abstract String getQuestionType();

    @Override
    public String toString() {
        return "[" + getQuestionType() + " / " + category + "] " + questionText + " (" + marks + " mk)";
    }
}
