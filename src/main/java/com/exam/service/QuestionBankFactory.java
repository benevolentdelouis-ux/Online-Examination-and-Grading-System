package com.exam.service;

import com.exam.model.MultipleChoiceQuestion;
import com.exam.model.Question;
import com.exam.model.ShortAnswerQuestion;
import com.exam.model.TrueFalseQuestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builds a default set of sample questions so the application has content
 * to demonstrate on first launch. Kept separate from the UI to keep
 * construction logic modular and reusable. Each question is tagged with a
 * category so the exam can optionally be filtered by topic.
 */
public final class QuestionBankFactory {

    public static final String CATEGORY_OOP = "OOP Concepts";
    public static final String CATEGORY_COLLECTIONS = "Collections";
    public static final String CATEGORY_JAVAFX = "JavaFX Basics";
    public static final String CATEGORY_EXCEPTIONS = "Exception Handling";

    private QuestionBankFactory() {
        // utility class - no instances
    }

    public static List<Question> createSampleQuestions() {
        List<Question> questions = new ArrayList<>();

        questions.add(new MultipleChoiceQuestion(
                "Which keyword is used to inherit a class in Java?",
                5,
                Arrays.asList("implements", "extends", "inherits", "super"),
                1,
                CATEGORY_OOP));

        questions.add(new MultipleChoiceQuestion(
                "Which OOP principle allows a subclass to provide its own implementation of a method?",
                5,
                Arrays.asList("Encapsulation", "Abstraction", "Polymorphism", "Composition"),
                2,
                CATEGORY_OOP));

        questions.add(new TrueFalseQuestion(
                "A Java class can extend more than one class directly.",
                5,
                false,
                CATEGORY_OOP));

        questions.add(new TrueFalseQuestion(
                "An abstract class can contain both abstract and concrete methods.",
                5,
                true,
                CATEGORY_OOP));

        questions.add(new MultipleChoiceQuestion(
                "Which collection type does NOT allow duplicate elements?",
                5,
                Arrays.asList("ArrayList", "LinkedList", "HashSet", "Stack"),
                2,
                CATEGORY_COLLECTIONS));

        questions.add(new ShortAnswerQuestion(
                "Name the JavaFX class every GUI application must extend.",
                10,
                "Application",
                CATEGORY_JAVAFX));

        questions.add(new TrueFalseQuestion(
                "In JavaFX, event handlers can be attached using lambda expressions.",
                5,
                true,
                CATEGORY_JAVAFX));

        questions.add(new MultipleChoiceQuestion(
                "Which keyword is used to handle exceptions that may be thrown by a block of code?",
                5,
                Arrays.asList("throw", "catch", "try", "finally"),
                2,
                CATEGORY_EXCEPTIONS));

        questions.add(new ShortAnswerQuestion(
                "What term describes bundling data and methods together while restricting direct access to fields?",
                10,
                "Encapsulation",
                CATEGORY_OOP));

        questions.add(new MultipleChoiceQuestion(
                "Which JavaFX layout arranges children in a single horizontal row?",
                5,
                Arrays.asList("VBox", "HBox", "GridPane", "StackPane"),
                1,
                CATEGORY_JAVAFX));

        return questions;
    }
}
