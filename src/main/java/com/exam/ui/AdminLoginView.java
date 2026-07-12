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
        root.setTop(buildHeader());
        root.setCenter(buildCenter());
        return root;
    }

    private Parent buildHeader() {
        VBox header = new VBox(4);
        header.getStyleClass().add("header-bar");
        Label title = new Label("Admin Login");
        title.getStyleClass().add("header-title");
        Label subtitle = new Label("Sign in to manage the question bank.");
        subtitle.getStyleClass().add("header-subtitle");
        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private Parent buildCenter() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setMaxWidth(420);

        Label formTitle = new Label("Restricted Area");
        formTitle.getStyleClass().add("section-title");

        Label hint = new Label("Demo credentials -> username: admin, password: admin123");
        hint.getStyleClass().add("field-label");
        hint.setWrapText(true);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);

        Label userLabel = new Label("Username:");
        userLabel.getStyleClass().add("field-label");
        usernameField.setPromptText("admin");

        Label passLabel = new Label("Password:");
        passLabel.getStyleClass().add("field-label");
        passwordField.setPromptText("Password");

        grid.addRow(0, userLabel, usernameField);
        grid.addRow(1, passLabel, passwordField);

        errorLabel.setStyle("-fx-text-fill: #d64545; -fx-font-weight: bold;");
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        HBox buttonRow = new HBox(12);
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

        card.getChildren().addAll(formTitle, hint, grid, errorLabel, buttonRow);

        BorderPane wrapper = new BorderPane();
        wrapper.setPadding(new Insets(40));
        wrapper.setCenter(card);
        BorderPane.setAlignment(card, Pos.TOP_CENTER);
        return wrapper;
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
            showError("Incorrect credentials. Too many failed attempts - returning to the welcome screen.");
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
