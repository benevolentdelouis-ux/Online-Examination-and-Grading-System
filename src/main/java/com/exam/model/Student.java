package com.exam.model;

/**
 * Represents the candidate sitting the exam. Fields are private with
 * validating setters, a standard example of encapsulation.
 */
public class Student {

    private String fullName;
    private String studentId;

    public Student(String fullName, String studentId) {
        setFullName(fullName);
        setStudentId(studentId);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name cannot be empty.");
        }
        this.fullName = fullName.trim();
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException("Student ID cannot be empty.");
        }
        this.studentId = studentId.trim();
    }

    @Override
    public String toString() {
        return fullName + " (" + studentId + ")";
    }
}
