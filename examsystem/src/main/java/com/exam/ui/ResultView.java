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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
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
        root.setTop(UIUtils.headerBar("Exam Results",
                result.getExam().getTitle() + "   ·   " + result.getStudent()));

        VBox center = new VBox(24);
        center.setPadding(new Insets(28));
        center.setMinHeight(Region.USE_PREF_SIZE);
        HBox summaryCard = (HBox) buildSummaryCard();
        Parent reviewCard = buildReviewCard();
        Parent buttons = buildButtons();
        center.getChildren().addAll(summaryCard, reviewCard, buttons);

        ScrollPane scrollPane = new ScrollPane(center);
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);
        return root;
    }

    private Parent buildSummaryCard() {
        HBox card = new HBox(32);
        card.getStyleClass().add("card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMinHeight(Region.USE_PREF_SIZE);

        Grade grade = result.getGrade();
        boolean passed = grade != Grade.F;

        StackPane badge = buildGradeBadge(grade, passed);

        VBox statsBlock = new VBox(10);
        VBox.setVgrow(statsBlock, javafx.scene.layout.Priority.ALWAYS);

        Label remark = new Label(grade.getRemark());
        remark.getStyleClass().add(passed ? "grade-remark-pass" : "grade-remark-fail");

        GridPane grid = new GridPane();
        grid.setHgap(28);
        grid.setVgap(8);
        grid.addRow(0, boldLabel("Marks Obtained:"), statValue(result.getMarksObtained() + " / " + result.getTotalMarks()));
        grid.addRow(1, boldLabel("Percentage:"), statValue(String.format("%.1f%%", result.getPercentage())));
        grid.addRow(2, boldLabel("Correct Answers:"),
                statValue(result.getRecords().stream().filter(AnswerRecord::isCorrect).count()
                        + " / " + result.getRecords().size()));

        statsBlock.getChildren().addAll(remark, grid);

        card.getChildren().addAll(badge, statsBlock);
        return card;
    }

    private StackPane buildGradeBadge(Grade grade, boolean passed) {
        Region circle = new Region();
        circle.setStyle("-fx-background-color: red;");
        circle.setMinSize(200, 200);
        circle.setPrefSize(200, 200);
        circle.setMaxSize(200, 200);

        Label letter = new Label(grade.name());
        letter.getStyleClass().add(passed ? "grade-badge-letter-pass" : "grade-badge-letter-fail");

        StackPane badge = new StackPane(circle, letter);
        badge.setMinSize(200, 200);
        badge.setPrefSize(200, 200);
        badge.setMaxSize(200, 200);
        return badge;
    }

    private Label boldLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("field-label");
        return label;
    }

    private Label statValue(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("stat-value");
        return label;
    }

    private Parent buildReviewCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");

        VBox titleBlock = new VBox(4,
                UIUtils.eyebrow("Full Breakdown"),
                sectionTitle("Answer Review"));

        TableView<AnswerRecord> table = new TableView<>();

        TableColumn<AnswerRecord, String> questionCol = new TableColumn<>("Question");
        questionCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getQuestion().getQuestionText()));
        questionCol.setPrefWidth(280);

        TableColumn<AnswerRecord, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getQuestion().getCategory()));
        categoryCol.setPrefWidth(120);

        TableColumn<AnswerRecord, String> yourAnswerCol = new TableColumn<>("Your Answer");
        yourAnswerCol.setCellValueFactory(new PropertyValueFactory<>("submittedAnswer"));
        yourAnswerCol.setPrefWidth(150);

        TableColumn<AnswerRecord, String> correctAnswerCol = new TableColumn<>("Correct Answer");
        correctAnswerCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getQuestion().getCorrectAnswerDisplay()));
        correctAnswerCol.setPrefWidth(150);

        TableColumn<AnswerRecord, String> resultCol = new TableColumn<>("Result");
        resultCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().isCorrect() ? "\u2713  Correct" : "\u2717  Incorrect"));
        resultCol.setPrefWidth(100);

        table.getColumns().setAll(questionCol, categoryCol, yourAnswerCol, correctAnswerCol, resultCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(javafx.collections.FXCollections.observableArrayList(result.getRecords()));
        table.setPrefHeight(290);

        card.getChildren().addAll(titleBlock, table);
        return card;
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }

    private Parent buildButtons() {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        Button exportBtn = UIUtils.secondaryButton("Export Results to File");
        Button newAttemptBtn = UIUtils.secondaryButton("Back to Welcome");
        Button exitBtn = UIUtils.dangerButton("Exit Application");

        exportBtn.setOnAction(e -> handleExport(exportBtn));
        newAttemptBtn.setOnAction(e -> app.showWelcomeScreen());
        exitBtn.setOnAction(e -> Platform.exit());

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        row.getChildren().addAll(exportBtn, newAttemptBtn, spacer, exitBtn);
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
