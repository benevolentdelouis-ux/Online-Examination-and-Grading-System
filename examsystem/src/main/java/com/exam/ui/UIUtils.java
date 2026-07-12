package com.exam.ui;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Optional;

/**
 * Small collection of static helper methods shared by every view class:
 * alert dialogs, styled buttons, and the recurring visual-identity pieces
 * (the header seal badge, eyebrow labels, dividers, aligned form grids).
 * Centralising this avoids repeating the same boilerplate in every screen
 * and keeps the look consistent across the whole application.
 */
public final class UIUtils {

    private UIUtils() {
    }

    public static void showError(String header, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showInfo(String header, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean confirm(String header, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Please confirm");
        alert.setHeaderText(header);
        alert.setContentText(message);
        Optional<ButtonType> choice = alert.showAndWait();
        return choice.isPresent() && choice.get() == ButtonType.OK;
    }

    public static Button primaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("btn-primary");
        return button;
    }

    public static Button secondaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("btn-secondary");
        return button;
    }

    public static Button dangerButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("btn-danger");
        return button;
    }

    /**
     * The small circular monogram shown in every header bar. It is the
     * one recurring visual signature tying every screen back to the same
     * "official examination" identity, drawn with plain shapes so no
     * external image asset is needed.
     */
    public static HBox sealBadge() {
        Region ring = new Region();
        ring.getStyleClass().add("seal-circle");
        ring.setMinSize(40, 40);
        ring.setPrefSize(40, 40);
        ring.setMaxSize(40, 40);

        Label initials = new Label("OE");
        initials.getStyleClass().add("seal-text");

        StackPane stack = new StackPane(ring, initials);
        stack.setMinSize(40, 40);
        stack.setPrefSize(40, 40);
        stack.setMaxSize(40, 40);

        HBox wrapper = new HBox(stack);
        wrapper.setPadding(new Insets(0, 4, 0, 0));
        return wrapper;
    }

    /** Small tracked, uppercase label used above a section title. */
    public static Label eyebrow(String text) {
        Label label = new Label(text.toUpperCase());
        label.getStyleClass().add("eyebrow");
        return label;
    }

    /** A 1px horizontal rule used to separate a form from its action buttons. */
    public static Region divider() {
        Region line = new Region();
        line.getStyleClass().add("divider");
        line.setPrefHeight(1);
        line.setMaxWidth(Double.MAX_VALUE);
        return line;
    }

    /** Standard header bar: seal badge + title on one row, plain subtitle below. */
    public static VBox headerBar(String title, String subtitle) {
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("header-subtitle");
        return headerBar(title, subtitleLabel);
    }

    /**
     * Standard header bar with a custom node in place of the subtitle row
     * (used by the exam screen, which shows the candidate name and a live
     * countdown timer side by side instead of plain text).
     */
    public static VBox headerBar(String title, javafx.scene.Node subtitleRow) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("header-title");

        HBox titleRow = new HBox(12, sealBadge(), titleLabel);
        titleRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox header = new VBox(8, titleRow, subtitleRow);
        header.getStyleClass().add("header-bar");
        return header;
    }

    /** Fixed-width, right-aligned label column for a form GridPane. */
    public static ColumnConstraints labelColumn(double width) {
        ColumnConstraints column = new ColumnConstraints();
        column.setMinWidth(width);
        column.setPrefWidth(width);
        column.setHalignment(HPos.RIGHT);
        return column;
    }

    /** Growing field column for a form GridPane, so inputs fill remaining width. */
    public static ColumnConstraints fieldColumn() {
        ColumnConstraints column = new ColumnConstraints();
        column.setHgrow(Priority.ALWAYS);
        return column;
    }
}
