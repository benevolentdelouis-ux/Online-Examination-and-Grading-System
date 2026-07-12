package com.exam.ui;

import com.exam.model.*;
import com.exam.service.ExamService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The screen a student sees while sitting the exam. One question is shown
 * at a time; the type of input control offered (radio buttons, toggle
 * group, text field) is chosen based on the concrete {@link Question}
 * subtype, while grading itself remains entirely polymorphic and lives in
 * {@link ExamService}.
 */
public class ExamView {

    private final ExamService examService;
    private final MainApp app;
    private final Student student;
    private final Exam exam;
    private final List<Question> questions;
    private final Map<Question, String> answers = new LinkedHashMap<>();

    private int currentIndex = 0;
    private int secondsRemaining;
    private Timeline timer;

    private Label questionCounterLabel;
    private Label questionTextLabel;
    private Label timerLabel;
    private ProgressBar progressBar;
    private VBox answerArea;
    private Button previousBtn;
    private Button nextBtn;
    private Button submitBtn;

    // Live references to the currently rendered answer control(s)
    private ToggleGroup mcqGroup;
    private ToggleGroup tfGroup;
    private TextField shortAnswerField;

    public ExamView(ExamService examService, MainApp app, Student student, Exam exam) {
        this.examService = examService;
        this.app = app;
        this.student = student;
        this.exam = exam;
        this.questions = exam.getQuestions();
        this.secondsRemaining = exam.getDurationMinutes() * 60;
    }

    public Parent getRoot() {
        BorderPane root = new BorderPane();
        root.setTop(buildHeader());
        root.setCenter(buildCenter());
        root.setBottom(buildNavigation());

        startTimer();
        renderQuestion(currentIndex);
        return root;
    }

    private Parent buildHeader() {
        VBox header = new VBox(6);
        header.getStyleClass().add("header-bar");

        Label title = new Label(exam.getTitle());
        title.getStyleClass().add("header-title");

        HBox infoRow = new HBox(24);
        Label studentLabel = new Label("Candidate: " + student);
        studentLabel.getStyleClass().add("header-subtitle");
        timerLabel = new Label();
        timerLabel.getStyleClass().add("timer-label");
        timerLabel.setStyle(timerLabel.getStyle() + "-fx-text-fill: #ffd166;");
        infoRow.getChildren().addAll(studentLabel, timerLabel);

        header.getChildren().addAll(title, infoRow);
        return header;
    }

    private Parent buildCenter() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setMaxWidth(640);

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(600);

        questionCounterLabel = new Label();
        questionCounterLabel.getStyleClass().add("field-label");

        questionTextLabel = new Label();
        questionTextLabel.getStyleClass().add("question-text");
        questionTextLabel.setWrapText(true);

        answerArea = new VBox(12);

        card.getChildren().addAll(progressBar, questionCounterLabel, questionTextLabel, answerArea);

        BorderPane wrapper = new BorderPane();
        wrapper.setPadding(new Insets(30));
        wrapper.setCenter(card);
        BorderPane.setAlignment(card, Pos.TOP_CENTER);
        return wrapper;
    }

    private Parent buildNavigation() {
        HBox nav = new HBox(14);
        nav.setPadding(new Insets(16, 30, 24, 30));
        nav.setAlignment(Pos.CENTER_RIGHT);

        previousBtn = UIUtils.secondaryButton("<< Previous");
        nextBtn = UIUtils.primaryButton("Next >>");
        submitBtn = UIUtils.dangerButton("Submit Exam");

        previousBtn.setOnAction(e -> goToQuestion(currentIndex - 1));
        nextBtn.setOnAction(e -> goToQuestion(currentIndex + 1));
        submitBtn.setOnAction(e -> handleSubmit(false));

        nav.getChildren().addAll(previousBtn, nextBtn, submitBtn);
        return nav;
    }

    private void goToQuestion(int newIndex) {
        saveCurrentAnswer();
        if (newIndex < 0 || newIndex >= questions.size()) {
            return;
        }
        currentIndex = newIndex;
        renderQuestion(currentIndex);
    }

    private void renderQuestion(int index) {
        Question question = questions.get(index);
        questionCounterLabel.setText("Question " + (index + 1) + " of " + questions.size()
                + "  |  " + question.getQuestionType() + "  |  " + question.getCategory()
                + "  |  " + question.getMarks() + " mark(s)");
        questionTextLabel.setText(question.getQuestionText());
        progressBar.setProgress((double) (index + 1) / questions.size());

        answerArea.getChildren().clear();
        mcqGroup = null;
        tfGroup = null;
        shortAnswerField = null;

        String previousAnswer = answers.get(question);

        if (question instanceof MultipleChoiceQuestion mcq) {
            mcqGroup = new ToggleGroup();
            List<String> options = mcq.getOptions();
            for (int i = 0; i < options.size(); i++) {
                RadioButton rb = new RadioButton(options.get(i));
                rb.setToggleGroup(mcqGroup);
                rb.setUserData(String.valueOf(i));
                if (previousAnswer != null && previousAnswer.equals(String.valueOf(i))) {
                    rb.setSelected(true);
                }
                answerArea.getChildren().add(rb);
            }

        } else if (question instanceof TrueFalseQuestion) {
            tfGroup = new ToggleGroup();
            RadioButton trueRb = new RadioButton("True");
            RadioButton falseRb = new RadioButton("False");
            trueRb.setToggleGroup(tfGroup);
            falseRb.setToggleGroup(tfGroup);
            trueRb.setUserData("true");
            falseRb.setUserData("false");
            if ("true".equals(previousAnswer)) {
                trueRb.setSelected(true);
            } else if ("false".equals(previousAnswer)) {
                falseRb.setSelected(true);
            }
            answerArea.getChildren().addAll(trueRb, falseRb);

        } else if (question instanceof ShortAnswerQuestion) {
            shortAnswerField = new TextField();
            shortAnswerField.setPromptText("Type your answer here");
            if (previousAnswer != null) {
                shortAnswerField.setText(previousAnswer);
            }
            answerArea.getChildren().add(shortAnswerField);
        }

        previousBtn.setDisable(index == 0);
        nextBtn.setDisable(index == questions.size() - 1);
    }

    private void saveCurrentAnswer() {
        Question question = questions.get(currentIndex);
        String value = null;

        if (mcqGroup != null && mcqGroup.getSelectedToggle() != null) {
            value = (String) mcqGroup.getSelectedToggle().getUserData();
        } else if (tfGroup != null && tfGroup.getSelectedToggle() != null) {
            value = (String) tfGroup.getSelectedToggle().getUserData();
        } else if (shortAnswerField != null) {
            value = shortAnswerField.getText();
        }

        if (value != null && !value.isBlank()) {
            answers.put(question, value);
        } else {
            answers.remove(question);
        }
    }

    private void startTimer() {
        updateTimerLabel();
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsRemaining--;
            updateTimerLabel();
            if (secondsRemaining <= 0) {
                timer.stop();
                UIUtils.showInfo("Time's up!", "Your exam is being submitted automatically.");
                handleSubmit(true);
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void updateTimerLabel() {
        int minutes = Math.max(secondsRemaining, 0) / 60;
        int seconds = Math.max(secondsRemaining, 0) % 60;
        timerLabel.setText(String.format("Time remaining: %02d:%02d", minutes, seconds));
    }

    private void handleSubmit(boolean forced) {
        saveCurrentAnswer();

        if (!forced) {
            long answered = questions.stream().filter(answers::containsKey).count();
            boolean confirmed = UIUtils.confirm(
                    "Submit exam?",
                    "You have answered " + answered + " of " + questions.size() + " question(s). "
                            + "Once submitted you cannot change your answers.");
            if (!confirmed) {
                return;
            }
        }

        if (timer != null) {
            timer.stop();
        }

        ExamResult result = examService.gradeExam(exam, student, answers);
        app.showResultScreen(result);
    }
}
