package com.exam.model;

import com.exam.exception.ExamConfigurationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a single exam paper: a title, a time limit and an ordered
 * collection of {@link Question}s. Uses an {@code ArrayList} internally to
 * demonstrate collection handling.
 */
public class Exam {

    private String title;
    private int durationMinutes;
    private final List<Question> questions = new ArrayList<>();

    public Exam(String title, int durationMinutes) {
        setTitle(title);
        setDurationMinutes(durationMinutes);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Exam title cannot be empty.");
        }
        this.title = title.trim();
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be a positive number of minutes.");
        }
        this.durationMinutes = durationMinutes;
    }

    public void addQuestion(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Cannot add a null question.");
        }
        questions.add(question);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
    }

    public List<Question> getQuestions() {
        return Collections.unmodifiableList(questions);
    }

    public int getTotalMarks() {
        int total = 0;
        for (Question q : questions) {
            total += q.getMarks();
        }
        return total;
    }

    /** Ensures the exam is in a state that can actually be sat by a student. */
    public void validateReadyToStart() throws ExamConfigurationException {
        if (questions.isEmpty()) {
            throw new ExamConfigurationException("This exam has no questions yet. Add at least one question.");
        }
    }
}
