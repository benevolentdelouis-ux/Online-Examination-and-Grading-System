package com.exam.ui;

import com.exam.model.AnswerRecord;
import com.exam.model.ExamResult;
import com.exam.model.Grade;
import com.exam.service.ReportExporter;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;

/**
 * Displays the outcome of a graded exam attempt: overall score, letter
 * grade and a detailed, question-by-question review.
 */
public class ResultView {

    private final MainApp app;
    private final ExamResult result;

    public ResultView(MainApp app, ExamResult result) {
        this.app = app;
        this.result = result;
    }

    public Parent getRoot() {
        BorderPane root = new BorderPane();
        root.setTop(buildHeader());

        VBox center = new VBox(20);
        center.setPadding(new Insets(24));
        center.getChildren().addAll(buildSummaryCard(), buildReviewCard(), buildButtons());

        ScrollPane scrollPane = new ScrollPane(center);
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);
        return root;
    }

    private Parent buildHeader() {
        VBox header = new VBox(4);
        header.getStyleClass().add("header-bar");
        Label title = new Label("Exam Results");
        title.getStyleClass().add("header-title");
        Label subtitle = new Label(result.getExam().getTitle() + "  |  " + result.getStudent());
        subtitle.getStyleClass().add("header-subtitle");
        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private Parent buildSummaryCard() {
        VBox card = new VBox(14);
        card.getStyleClass().add("card");

        Grade grade = result.getGrade();
        boolean passed = grade != Grade.F;

        Label gradeLabel = new Label("Grade: " + grade + " - " + grade.getRemark());
        gradeLabel.getStyleClass().add(passed ? "grade-pass" : "grade-fail");

        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(8);
        grid.addRow(0, boldLabel("Marks Obtained:"), new Label(result.getMarksObtained() + " / " + result.getTotalMarks()));
        grid.addRow(1, boldLabel("Percentage:"), new Label(String.format("%.1f%%", result.getPercentage())));
        grid.addRow(2, boldLabel("Questions Answered Correctly:"),
                new Label(result.getRecords().stream().filter(AnswerRecord::isCorrect).count()
                        + " / " + result.getRecords().size()));

        card.getChildren().addAll(gradeLabel, grid);
        return card;
    }

    private Label boldLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("field-label");
        label.setStyle("-fx-font-weight: bold;");
        return label;
    }

    private Parent buildReviewCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");

        Label sectionTitle = new Label("Answer Review");
        sectionTitle.getStyleClass().add("section-title");

        TableView<AnswerRecord> table = new TableView<>();

        TableColumn<AnswerRecord, String> questionCol = new TableColumn<>("Question");
        questionCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getQuestion().getQuestionText()));
        questionCol.setPrefWidth(260);

        TableColumn<AnswerRecord, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getQuestion().getCategory()));
        categoryCol.setPrefWidth(120);

        TableColumn<AnswerRecord, String> yourAnswerCol = new TableColumn<>("Your Answer");
        yourAnswerCol.setCellValueFactory(new PropertyValueFactory<>("submittedAnswer"));
        yourAnswerCol.setPrefWidth(160);

        TableColumn<AnswerRecord, String> correctAnswerCol = new TableColumn<>("Correct Answer");
        correctAnswerCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getQuestion().getCorrectAnswerDisplay()));
        correctAnswerCol.setPrefWidth(160);

        TableColumn<AnswerRecord, String> resultCol = new TableColumn<>("Result");
        resultCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().isCorrect() ? "Correct" : "Incorrect"));
        resultCol.setPrefWidth(90);

        table.getColumns().setAll(questionCol, categoryCol, yourAnswerCol, correctAnswerCol, resultCol);
        table.setItems(javafx.collections.FXCollections.observableArrayList(result.getRecords()));
        table.setPrefHeight(280);

        card.getChildren().addAll(sectionTitle, table);
        return card;
    }

    private Parent buildButtons() {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_RIGHT);

        Button exportBtn = UIUtils.secondaryButton("Export Results to File");
        Button newAttemptBtn = UIUtils.secondaryButton("Back to Welcome");
        Button exitBtn = UIUtils.dangerButton("Exit Application");

        exportBtn.setOnAction(e -> handleExport(exportBtn));
        newAttemptBtn.setOnAction(e -> app.showWelcomeScreen());
        exitBtn.setOnAction(e -> Platform.exit());

        row.getChildren().addAll(exportBtn, newAttemptBtn, exitBtn);
        return row;
    }

    private void handleExport(Button sourceButton) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Exam Result Report");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Report (*.txt)", "*.txt"));
        String suggestedName = "ExamResult_" + result.getStudent().getStudentId().replaceAll("[^A-Za-z0-9]", "_") + ".txt";
        fileChooser.setInitialFileName(suggestedName);

        Window window = sourceButton.getScene().getWindow();
        File file = fileChooser.showSaveDialog(window);
        if (file == null) {
            return; // user cancelled the dialog
        }

        try {
            ReportExporter.exportToFile(result, file);
            UIUtils.showInfo("Export successful", "The result report was saved to:\n" + file.getAbsolutePath());
        } catch (IOException e) {
            UIUtils.showError("Could not save report", "An error occurred while writing the file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            UIUtils.showError("Could not save report", e.getMessage());
        }
    }
}
