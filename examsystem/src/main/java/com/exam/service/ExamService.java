package com.exam.service;

import com.exam.exception.InvalidAnswerException;
import com.exam.model.AnswerRecord;
import com.exam.model.Exam;
import com.exam.model.ExamResult;
import com.exam.model.Question;
import com.exam.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Central service that owns the question bank and knows how to grade a
 * completed exam attempt. Keeping this logic out of the UI classes keeps
 * the project modular: views only ever talk to this service.
 */
public class ExamService {

    private final ObservableList<Question> questionBank =
            FXCollections.observableArrayList(QuestionBankFactory.createSampleQuestions());

    public ObservableList<Question> getQuestionBank() {
        return questionBank;
    }

    public void addQuestion(Question question) {
        questionBank.add(question);
    }

    public void removeQuestion(Question question) {
        questionBank.remove(question);
    }

    /**
     * Returns the distinct categories currently present in the question
     * bank, sorted alphabetically, for populating filter/selection controls.
     */
    public List<String> getCategories() {
        return questionBank.stream()
                .map(Question::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Returns the questions belonging to a given category, or the whole
     * bank if {@code category} is null, blank, or equal to "All Categories".
     */
    public List<Question> getQuestionsByCategory(String category) {
        if (category == null || category.isBlank() || "All Categories".equals(category)) {
            return new ArrayList<>(questionBank);
        }
        return questionBank.stream()
                .filter(q -> q.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    /**
     * Grades a completed attempt. Every answer is validated through the
     * question's own {@code isCorrect} method (polymorphic dispatch); any
     * answer that fails validation (missing / malformed) is caught and
     * simply recorded as incorrect rather than crashing the grading run.
     */
    public ExamResult gradeExam(Exam exam, Student student, Map<Question, String> studentAnswers) {
        ExamResult result = new ExamResult(student, exam);

        for (Question question : exam.getQuestions()) {
            String submitted = studentAnswers.get(question);
            boolean correct;
            try {
                correct = question.isCorrect(submitted);
            } catch (InvalidAnswerException e) {
                // Missing or malformed answer -> treated as incorrect,
                // but we do not let the exception propagate and crash grading.
                correct = false;
            }
            result.addRecord(new AnswerRecord(question, submitted, correct));
        }
        return result;
    }

    /**
     * Convenience factory for building an exam from a chosen subset of the
     * question bank, preserving the order questions were selected in.
     */
    public Exam buildExam(String title, int durationMinutes, Iterable<Question> selected) {
        Exam exam = new Exam(title, durationMinutes);
        Map<Integer, Question> ordered = new LinkedHashMap<>();
        for (Question q : selected) {
            ordered.put(q.getId(), q);
        }
        for (Question q : ordered.values()) {
            exam.addQuestion(q);
        }
        return exam;
    }
}
