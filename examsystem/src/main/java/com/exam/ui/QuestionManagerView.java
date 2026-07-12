package com.exam.ui;

import com.exam.model.MultipleChoiceQuestion;
import com.exam.model.Question;
import com.exam.model.ShortAnswerQuestion;
import com.exam.model.TrueFalseQuestion;
import com.exam.service.ExamService;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Screen for administering the question bank: viewing existing questions
 * in a table and adding new ones of any of the three supported types.
 * The form dynamically swaps its type-specific controls based on the
 * selected question type, demonstrating both event-driven programming and
 * polymorphic construction of {@link Question} subclasses.
 */
public class QuestionManagerView {

    private static final String TYPE_MCQ = "Multiple Choice";
    private static final String TYPE_TF = "True / False";
    private static final String TYPE_SHORT = "Short Answer";
    private static final String ALL_CATEGORIES = "All Categories";
    private static final double LABEL_WIDTH = 150;

    private final ExamService examService;
    private final MainApp app;

    private final TableView<Question> table = new TableView<>();
    private final ComboBox<String> filterCombo = new ComboBox<>();
    private final TextField textField = new TextField();
    private final TextField marksField = new TextField();
    private final ComboBox<String> categoryCombo = new ComboBox<>();
    private final ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList(TYPE_MCQ, TYPE_TF, TYPE_SHORT));
    private final VBox typeSpecificPane = new VBox(10);

    // Multiple choice controls
    private final TextField[] optionFields = new TextField[4];
    private final Spinner<Integer> correctOptionSpinner =
            new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 4, 1));

    // True/False controls
    private final ToggleGroup trueFalseGroup = new ToggleGroup();
    private final RadioButton trueRadio = new RadioButton("True");
    private final RadioButton falseRadio = new RadioButton("False");

    // Short answer controls
    private final TextField modelAnswerField = new TextField();

    public QuestionManagerView(ExamService examService, MainApp app) {
        this.examService = examService;
        this.app = app;
    }

    public Parent getRoot() {
        BorderPane root = new BorderPane();
        root.setTop(UIUtils.headerBar("Question Bank Management", "Add, review, or remove exam questions."));

        VBox center = new VBox(24);
        center.setPadding(new Insets(28));
        center.getChildren().addAll(buildTableCard(), buildFormCard());

        ScrollPane scrollPane = new ScrollPane(center);
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);
        return root;
    }

    @SuppressWarnings("unchecked")
    private Parent buildTableCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");

        VBox titleBlock = new VBox(4,
                UIUtils.eyebrow("Question Bank"),
                sectionTitle("Current Questions"));

        HBox filterRow = new HBox(10);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        Label filterLabel = new Label("Filter by category:");
        filterLabel.getStyleClass().add("field-label");
        refreshFilterOptions();
        filterCombo.setValue(ALL_CATEGORIES);
        filterRow.getChildren().addAll(filterLabel, filterCombo);

        TableColumn<Question, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()));
        idCol.setPrefWidth(50);

        TableColumn<Question, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("questionType"));
        typeCol.setPrefWidth(115);

        TableColumn<Question, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(135);

        TableColumn<Question, String> textCol = new TableColumn<>("Question");
        textCol.setCellValueFactory(new PropertyValueFactory<>("questionText"));
        textCol.setPrefWidth(330);

        TableColumn<Question, Number> marksCol = new TableColumn<>("Marks");
        marksCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getMarks()));
        marksCol.setPrefWidth(60);

        table.getColumns().setAll(idCol, typeCol, categoryCol, textCol, marksCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        FilteredList<Question> filteredQuestions = new FilteredList<>(examService.getQuestionBank(), q -> true);
        filterCombo.valueProperty().addListener((obs, oldVal, newVal) -> filteredQuestions.setPredicate(q ->
                ALL_CATEGORIES.equals(newVal) || newVal == null || q.getCategory().equals(newVal)));
        table.setItems(filteredQuestions);
        table.setPrefHeight(250);
        table.setPlaceholder(new Label("No questions yet. Add one below."));

        Button removeBtn = UIUtils.dangerButton("Remove Selected");
        removeBtn.setOnAction(e -> handleRemoveSelected());

        HBox actionRow = new HBox(removeBtn);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(titleBlock, filterRow, table, actionRow);
        return card;
    }

    private void refreshFilterOptions() {
        List<String> options = new ArrayList<>();
        options.add(ALL_CATEGORIES);
        options.addAll(examService.getCategories());
        filterCombo.setItems(FXCollections.observableArrayList(options));
    }

    private Parent buildFormCard() {
        VBox card = new VBox(18);
        card.getStyleClass().add("card");

        VBox titleBlock = new VBox(4,
                UIUtils.eyebrow("New Entry"),
                sectionTitle("Add New Question"));

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(14);
        grid.getColumnConstraints().addAll(UIUtils.labelColumn(LABEL_WIDTH), UIUtils.fieldColumn());

        typeCombo.setValue(TYPE_MCQ);
        typeCombo.setMaxWidth(Double.MAX_VALUE);
        typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> rebuildTypeSpecificControls(newVal));

        textField.setPromptText("Enter the question text");
        marksField.setPromptText("e.g. 5");

        categoryCombo.setEditable(true);
        categoryCombo.setMaxWidth(Double.MAX_VALUE);
        categoryCombo.setItems(FXCollections.observableArrayList(examService.getCategories()));
        categoryCombo.setPromptText("Type a new one, or pick an existing category");

        grid.addRow(0, formLabel("Question Type:"), typeCombo);
        grid.addRow(1, formLabel("Question Text:"), textField);
        grid.addRow(2, formLabel("Marks:"), marksField);
        grid.addRow(3, formLabel("Category:"), categoryCombo);

        trueRadio.setToggleGroup(trueFalseGroup);
        falseRadio.setToggleGroup(trueFalseGroup);
        trueRadio.setSelected(true);

        rebuildTypeSpecificControls(TYPE_MCQ);

        HBox buttonRow = new HBox(12);
        buttonRow.setAlignment(Pos.CENTER_LEFT);
        Button addBtn = UIUtils.primaryButton("Add Question");
        Button backBtn = UIUtils.secondaryButton("Back to Welcome");
        addBtn.setOnAction(e -> handleAddQuestion());
        backBtn.setOnAction(e -> app.showWelcomeScreen());
        buttonRow.getChildren().addAll(addBtn, backBtn);

        card.getChildren().addAll(titleBlock, grid, typeSpecificPane, UIUtils.divider(), buttonRow);
        return card;
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }

    private Label formLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("field-label");
        return label;
    }

    /** Swaps the controls shown below the common fields based on question type. */
    private void rebuildTypeSpecificControls(String type) {
        typeSpecificPane.getChildren().clear();

        if (TYPE_MCQ.equals(type)) {
            Label hint = new Label("Enter the 4 options and pick the correct option number (1–4):");
            hint.getStyleClass().add("hint-text");

            GridPane optionsGrid = new GridPane();
            optionsGrid.setHgap(16);
            optionsGrid.setVgap(10);
            optionsGrid.getColumnConstraints().addAll(UIUtils.labelColumn(LABEL_WIDTH), UIUtils.fieldColumn());
            for (int i = 0; i < 4; i++) {
                optionFields[i] = new TextField();
                optionFields[i].setPromptText("Option " + (i + 1));
                optionsGrid.addRow(i, formLabel("Option " + (i + 1) + ":"), optionFields[i]);
            }

            GridPane correctRow = new GridPane();
            correctRow.setHgap(16);
            correctRow.getColumnConstraints().addAll(UIUtils.labelColumn(LABEL_WIDTH), UIUtils.fieldColumn());
            correctRow.addRow(0, formLabel("Correct option number:"), correctOptionSpinner);

            typeSpecificPane.getChildren().addAll(hint, optionsGrid, correctRow);

        } else if (TYPE_TF.equals(type)) {
            Label hint = new Label("Select the correct answer:");
            hint.getStyleClass().add("hint-text");
            HBox radioRow = new HBox(20, trueRadio, falseRadio);
            radioRow.setAlignment(Pos.CENTER_LEFT);
            typeSpecificPane.getChildren().addAll(hint, radioRow);

        } else if (TYPE_SHORT.equals(type)) {
            Label hint = new Label("Model (correct) answer:");
            hint.getStyleClass().add("hint-text");
            modelAnswerField.setPromptText("Expected answer");
            typeSpecificPane.getChildren().addAll(hint, modelAnswerField);
        }
    }

    private void handleAddQuestion() {
        try {
            String text = textField.getText();
            int marks = parseMarks(marksField.getText());
            String type = typeCombo.getValue();
            String category = categoryCombo.getEditor().getText();
            if (category == null || category.isBlank()) {
                category = Question.DEFAULT_CATEGORY;
            }

            Question question = switch (type) {
                case TYPE_MCQ -> buildMultipleChoiceQuestion(text, marks, category);
                case TYPE_TF -> new TrueFalseQuestion(text, marks, trueRadio.isSelected(), category);
                case TYPE_SHORT -> new ShortAnswerQuestion(text, marks, modelAnswerField.getText(), category);
                default -> throw new IllegalArgumentException("Unknown question type selected.");
            };

            examService.addQuestion(question);
            UIUtils.showInfo("Question added", "The question was added to the bank successfully.");
            clearForm();
            refreshFilterOptions();
            categoryCombo.setItems(FXCollections.observableArrayList(examService.getCategories()));

        } catch (IllegalArgumentException e) {
            UIUtils.showError("Could not add question", e.getMessage());
        }
    }

    private MultipleChoiceQuestion buildMultipleChoiceQuestion(String text, int marks, String category) {
        List<String> options = Arrays.asList(
                optionFields[0].getText(), optionFields[1].getText(),
                optionFields[2].getText(), optionFields[3].getText());
        int correctIndex = correctOptionSpinner.getValue() - 1;
        return new MultipleChoiceQuestion(text, marks, options, correctIndex, category);
    }

    private int parseMarks(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Marks cannot be empty.");
        }
        try {
            int value = Integer.parseInt(raw.trim());
            if (value <= 0) {
                throw new IllegalArgumentException("Marks must be a positive whole number.");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Marks must be a whole number, e.g. 5.");
        }
    }

    private void handleRemoveSelected() {
        Question selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIUtils.showError("Nothing selected", "Please select a question in the table to remove.");
            return;
        }
        if (UIUtils.confirm("Remove question?", "This will permanently remove: \"" + selected.getQuestionText() + "\"")) {
            examService.removeQuestion(selected);
        }
    }

    private void clearForm() {
        textField.clear();
        marksField.clear();
        for (TextField f : optionFields) {
            if (f != null) {
                f.clear();
            }
        }
        correctOptionSpinner.getValueFactory().setValue(1);
        trueRadio.setSelected(true);
        modelAnswerField.clear();
        categoryCombo.getEditor().clear();
    }
}
