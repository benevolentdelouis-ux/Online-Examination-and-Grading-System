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
        root.setTop(buildHeader());
        root.setCenter(buildCenter());
        return root;
    }

    private Parent buildHeader() {
        VBox header = new VBox(4);
        header.getStyleClass().add("header-bar");
        Label title = new Label("Online Examination and Grading System");
        title.getStyleClass().add("header-title");
        Label subtitle = new Label("Register below to begin your exam, or manage the question bank.");
        subtitle.getStyleClass().add("header-subtitle");
        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private Parent buildCenter() {
        VBox card = new VBox(18);
        card.getStyleClass().add("card");
        card.setMaxWidth(480);
        card.setAlignment(Pos.TOP_LEFT);

        Label formTitle = new Label("Candidate Registration");
        formTitle.getStyleClass().add("section-title");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);

        Label nameLabel = new Label("Full Name:");
        nameLabel.getStyleClass().add("field-label");
        nameField.setPromptText("e.g. Ama Serwaa");

        Label idLabel = new Label("Student ID:");
        idLabel.getStyleClass().add("field-label");
        idField.setPromptText("e.g. UG/2024/0113");

        Label durationLabel = new Label("Exam Duration (minutes):");
        durationLabel.getStyleClass().add("field-label");
        durationSpinner.setEditable(true);

        Label categoryLabel = new Label("Exam Category:");
        categoryLabel.getStyleClass().add("field-label");
        refreshCategoryOptions();
        categoryCombo.setValue(ALL_CATEGORIES);

        grid.addRow(0, nameLabel, nameField);
        grid.addRow(1, idLabel, idField);
        grid.addRow(2, durationLabel, durationSpinner);
        grid.addRow(3, categoryLabel, categoryCombo);

        Label bankInfo = new Label();
        bankInfo.getStyleClass().add("field-label");
        bankInfo.textProperty().bind(
                javafx.beans.binding.Bindings.size(examService.getQuestionBank())
                        .asString("Question bank currently has %d question(s)."));

        HBox buttonRow = new HBox(12);
        var manageBtn = UIUtils.secondaryButton("Manage Questions");
        var startBtn = UIUtils.primaryButton("Start Exam");
        buttonRow.getChildren().addAll(manageBtn, startBtn);

        manageBtn.setOnAction(e -> app.showAdminLogin());
        startBtn.setOnAction(e -> handleStartExam());

        card.getChildren().addAll(formTitle, grid, bankInfo, buttonRow);

        BorderPane wrapper = new BorderPane();
        wrapper.setPadding(new Insets(40));
        wrapper.setCenter(card);
        BorderPane.setAlignment(card, Pos.TOP_CENTER);
        return wrapper;
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
