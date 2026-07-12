package com.exam.ui;

import com.exam.model.Exam;
import com.exam.model.ExamResult;
import com.exam.model.Student;
import com.exam.service.ExamService;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point of the Online Examination and Grading System.
 *
 * <p>This class owns the single {@link Stage} used throughout the
 * application's lifetime and exposes simple {@code showXxx} navigation
 * methods that swap the {@link Scene}'s root node. Every screen is a plain
 * view class that builds its own {@code Parent} and is handed a reference
 * back to this class so it can navigate onward - a small, dependency-light
 * alternative to a full navigation framework.</p>
 */
public class MainApp extends Application {

    private static final double WINDOW_WIDTH = 960;
    private static final double WINDOW_HEIGHT = 680;

    private final ExamService examService = new ExamService();
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Online Examination and Grading System");
        stage.setMinWidth(820);
        stage.setMinHeight(600);
        showWelcomeScreen();
        stage.show();
    }

    public void showWelcomeScreen() {
        WelcomeView view = new WelcomeView(examService, this);
        setRoot(view.getRoot());
    }

    public void showAdminLogin() {
        AdminLoginView view = new AdminLoginView(this);
        setRoot(view.getRoot());
    }

    public void showQuestionManager() {
        QuestionManagerView view = new QuestionManagerView(examService, this);
        setRoot(view.getRoot());
    }

    public void showExamScreen(Student student, Exam exam) {
        ExamView view = new ExamView(examService, this, student, exam);
        setRoot(view.getRoot());
    }

    public void showResultScreen(ExamResult result) {
        ResultView view = new ResultView(this, result);
        setRoot(view.getRoot());
    }

    private void setRoot(Parent root) {
        Scene scene = primaryStage.getScene();
        if (scene == null) {
            scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
            primaryStage.setScene(scene);
        } else {
            scene.setRoot(root);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
