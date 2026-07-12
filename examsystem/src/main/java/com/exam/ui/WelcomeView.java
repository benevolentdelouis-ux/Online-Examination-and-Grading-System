package com.exam.ui;

import com.exam.exception.ExamConfigurationException;
import com.exam.model.Exam;
import com.exam.model.Student;
import com.exam.service.ExamService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * The first screen shown to the user: student registration (full name and
 * ID) plus entry points into either the exam itself or the (login-gated)
 * question management screen.
 */
public class WelcomeView {

    private static final String ALL_CATEGORIES = "All Categories";
    private static final double LABEL_WIDTH = 170;

    private final ExamService examService;
    private final MainApp app;

    private final TextField nameField = new TextField();
    private final TextField idField = new TextField();
    private final Spinner<Integer> durationSpinner =
            new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 120, 15, 5));
    private final ComboBox<String> categoryCombo = new ComboBox<>();

    public WelcomeView(ExamService examService, MainApp app) {
        this.examService = examService;
        this.app = app;
    }

    public Parent getRoot() {
        BorderPane root = new BorderPane();
        root.setTop(UIUtils.headerBar(
                "Online Examination and Grading System",
                "Register below to begin your exam, or manage the question bank."));
        root.setCenter(buildCenter());
        return root;
    }

    private Parent buildCenter() {
        VBox card = new VBox(20);
        card.getStyleClass().add("card");
        card.setMaxWidth(520);
        card.setAlignment(Pos.TOP_LEFT);

        VBox titleBlock = new VBox(4,
                UIUtils.eyebrow("Step 1 of 2"),
                titledSection("Candidate Registration"));

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(14);
        grid.getColumnConstraints().addAll(UIUtils.labelColumn(LABEL_WIDTH), UIUtils.fieldColumn());

        nameField.setPromptText("e.g. Ama Serwaa");
        idField.setPromptText("e.g. UG/2024/0113");
        durationSpinner.setEditable(true);
        durationSpinner.setMaxWidth(Double.MAX_VALUE);
        categoryCombo.setMaxWidth(Double.MAX_VALUE);

        refreshCategoryOptions();
        categoryCombo.setValue(ALL_CATEGORIES);

        grid.addRow(0, formLabel("Full Name:"), nameField);
        grid.addRow(1, formLabel("Student ID:"), idField);
        grid.addRow(2, formLabel("Exam Duration (minutes):"), durationSpinner);
        grid.addRow(3, formLabel("Exam Category:"), categoryCombo);

        Label bankInfo = new Label();
        bankInfo.getStyleClass().add("hint-text");
        bankInfo.textProperty().bind(
                javafx.beans.binding.Bindings.size(examService.getQuestionBank())
                        .asString("Question bank currently has %d question(s) available."));

        HBox buttonRow = new HBox(12);
        buttonRow.setAlignment(Pos.CENTER_LEFT);
        var manageBtn = UIUtils.secondaryButton("Manage Questions");
        var startBtn = UIUtils.primaryButton("Start Exam");
        buttonRow.getChildren().addAll(manageBtn, startBtn);

        manageBtn.setOnAction(e -> app.showAdminLogin());
        startBtn.setOnAction(e -> handleStartExam());

        card.getChildren().addAll(titleBlock, grid, bankInfo, UIUtils.divider(), buttonRow);

        BorderPane wrapper = new BorderPane();
        wrapper.setPadding(new Insets(44));
        wrapper.setCenter(card);
        BorderPane.setAlignment(card, Pos.TOP_CENTER);
        return wrapper;
    }

    private Label titledSection(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }

    private Label formLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("field-label");
        return label;
    }

    private void refreshCategoryOptions() {
        List<String> options = new ArrayList<>();
        options.add(ALL_CATEGORIES);
        options.addAll(examService.getCategories());
        categoryCombo.setItems(FXCollections.observableArrayList(options));
    }

    private void handleStartExam() {
        try {
            Student student = new Student(nameField.getText(), idField.getText());

            String selectedCategory = categoryCombo.getValue();
            List<com.exam.model.Question> pool = examService.getQuestionsByCategory(selectedCategory);

            String examTitle = ALL_CATEGORIES.equals(selectedCategory) || selectedCategory == null
                    ? "OOP & JavaFX Assessment"
                    : selectedCategory + " Assessment";

            Exam exam = examService.buildExam(examTitle, durationSpinner.getValue(), pool);

            exam.validateReadyToStart();

            app.showExamScreen(student, exam);
        } catch (IllegalArgumentException e) {
            UIUtils.showError("Please correct the form", e.getMessage());
        } catch (ExamConfigurationException e) {
            UIUtils.showError("Exam not ready", e.getMessage());
        }
    }
}
