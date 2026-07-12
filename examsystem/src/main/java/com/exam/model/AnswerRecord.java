package com.exam.model;

/**
 * Captures what a student answered for one question and whether it was
 * correct, so the results screen can show a full review breakdown.
 */
public class AnswerRecord {

    private final Question question;
    private final String submittedAnswer;
    private final boolean correct;

    public AnswerRecord(Question question, String submittedAnswer, boolean correct) {
        this.question = question;
        this.submittedAnswer = submittedAnswer;
        this.correct = correct;
    }

    public Question getQuestion() {
        return question;
    }

    public String getSubmittedAnswer() {
        return (submittedAnswer == null || submittedAnswer.isBlank()) ? "(no answer)" : submittedAnswer;
    }

    public boolean isCorrect() {
        return correct;
    }
}
