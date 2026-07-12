package com.exam.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The outcome of a student sitting an {@link Exam}: marks obtained, the
 * derived percentage and {@link Grade}, plus a detailed per-question
 * breakdown for review.
 */
public class ExamResult {

    private final Student student;
    private final Exam exam;
    private final List<AnswerRecord> records = new ArrayList<>();
    private int marksObtained;

    public ExamResult(Student student, Exam exam) {
        this.student = student;
        this.exam = exam;
    }

    public void addRecord(AnswerRecord record) {
        records.add(record);
        if (record.isCorrect()) {
            marksObtained += record.getQuestion().getMarks();
        }
    }

    public Student getStudent() {
        return student;
    }

    public Exam getExam() {
        return exam;
    }

    public List<AnswerRecord> getRecords() {
        return Collections.unmodifiableList(records);
    }

    public int getMarksObtained() {
        return marksObtained;
    }

    public int getTotalMarks() {
        return exam.getTotalMarks();
    }

    public double getPercentage() {
        int total = getTotalMarks();
        return total == 0 ? 0.0 : (marksObtained * 100.0) / total;
    }

    public Grade getGrade() {
        return Grade.fromPercentage(getPercentage());
    }
}
