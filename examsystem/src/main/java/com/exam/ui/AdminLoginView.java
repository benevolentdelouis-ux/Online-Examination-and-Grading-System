package com.exam.ui;

import com.exam.service.AdminAuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Simple login gate shown before the Question Bank Management screen.
 * Demonstrates a basic authentication flow, input validation, and event
 * handling (both button clicks and the Enter key submit the form).
 */
public class AdminLoginView {

    private static final double LABEL_WIDTH = 100;

    private final MainApp app;
    private final AdminAuthService authService = new AdminAuthService();

    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Label errorLabel = new Label();

    private int failedAttempts = 0;

    public AdminLoginView(MainApp app) {
        this.app = app;
    }

    public Parent getRoot() {
        BorderPane root = new BorderPane();
        root.setTop(UIUtils.headerBar("Admin Login", "Sign in to manage the question bank."));
        root.setCenter(buildCenter());
        return root;
    }

    private Parent buildCenter() {
        VBox card = new VBox(18);
        card.getStyleClass().add("card");
        card.setMaxWidth(440);

        VBox titleBlock = new VBox(4,
                UIUtils.eyebrow("Restricted Area"),
                sectionTitle("Administrator Sign In"));

        Label hint = new Label("Demo credentials — username: admin, password: admin123");
        hint.getStyleClass().add("hint-text");
        hint.setWrapText(true);

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(14);
        grid.getColumnConstraints().addAll(UIUtils.labelColumn(LABEL_WIDTH), UIUtils.fieldColumn());

        usernameField.setPromptText("admin");
        passwordField.setPromptText("Password");

        grid.addRow(0, formLabel("Username:"), usernameField);
        grid.addRow(1, formLabel("Password:"), passwordField);

        errorLabel.getStyleClass().add("error-text");
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        HBox buttonRow = new HBox(12);
        buttonRow.setAlignment(Pos.CENTER_LEFT);
        var loginBtn = UIUtils.primaryButton("Log In");
        var cancelBtn = UIUtils.secondaryButton("Cancel");
        buttonRow.getChildren().addAll(loginBtn, cancelBtn);

        loginBtn.setOnAction(e -> handleLogin());
        cancelBtn.setOnAction(e -> app.showWelcomeScreen());
        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });

        card.getChildren().addAll(titleBlock, hint, grid, errorLabel, UIUtils.divider(), buttonRow);

        BorderPane wrapper = new BorderPane();
        wrapper.setPadding(new Insets(44));
        wrapper.setCenter(card);
        BorderPane.setAlignment(card, Pos.TOP_CENTER);
        return wrapper;
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

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            showError("Please enter both a username and a password.");
            return;
        }

        if (authService.authenticate(username, password)) {
            app.showQuestionManager();
            return;
        }

        failedAttempts++;
        if (failedAttempts >= 3) {
            showError("Incorrect credentials. Too many failed attempts — returning to the welcome screen.");
            app.showWelcomeScreen();
        } else {
            showError("Incorrect username or password. Please try again.");
            passwordField.clear();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}
