package com.exam.service;

import com.exam.model.AnswerRecord;
import com.exam.model.ExamResult;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Writes a human-readable text report of a graded exam attempt to disk.
 * Kept separate from the UI so the file-writing / formatting logic can be
 * reused or unit tested independently of any JavaFX control.
 */
public final class ReportExporter {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private ReportExporter() {
        // utility class - no instances
    }

    /**
     * Writes a formatted report of the given result to {@code file}.
     *
     * @throws IOException if the file cannot be created or written to
     *                      (e.g. invalid path, disk full, permission denied)
     */
    public static void exportToFile(ExamResult result, File file) throws IOException {
        if (result == null) {
            throw new IllegalArgumentException("Cannot export a null result.");
        }
        if (file == null) {
            throw new IllegalArgumentException("No destination file was chosen.");
        }

        try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
            writer.println("=".repeat(60));
            writer.println("ONLINE EXAMINATION AND GRADING SYSTEM - RESULT REPORT");
            writer.println("=".repeat(60));
            writer.println("Generated: " + LocalDateTime.now().format(TIMESTAMP_FORMAT));
            writer.println();

            writer.println("Candidate   : " + result.getStudent());
            writer.println("Exam        : " + result.getExam().getTitle());
            writer.println("-".repeat(60));
            writer.println(String.format("Marks Obtained : %d / %d", result.getMarksObtained(), result.getTotalMarks()));
            writer.println(String.format("Percentage     : %.1f%%", result.getPercentage()));
            writer.println(String.format("Grade          : %s (%s)", result.getGrade(), result.getGrade().getRemark()));
            writer.println("-".repeat(60));
            writer.println();

            writer.println("ANSWER REVIEW");
            writer.println("-".repeat(60));
            int number = 1;
            for (AnswerRecord record : result.getRecords()) {
                writer.println(number + ". [" + record.getQuestion().getCategory() + "] "
                        + record.getQuestion().getQuestionText());
                writer.println("   Your Answer    : " + record.getSubmittedAnswer());
                writer.println("   Correct Answer : " + record.getQuestion().getCorrectAnswerDisplay());
                writer.println("   Result         : " + (record.isCorrect() ? "CORRECT" : "INCORRECT"));
                writer.println();
                number++;
            }

            writer.println("=".repeat(60));
            writer.println("End of report.");
        }
    }
}
