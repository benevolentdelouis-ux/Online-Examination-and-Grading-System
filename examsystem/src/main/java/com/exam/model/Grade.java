package com.exam.model;

/**
 * Letter grade bands. Each constant carries the minimum percentage
 * required to attain it, so the enum itself knows how to classify a score
 * (a small, self-contained example of encapsulating behaviour with data).
 */
public enum Grade {
    A(80, "Excellent"),
    B(70, "Very Good"),
    C(60, "Good"),
    D(50, "Pass"),
    E(40, "Weak Pass"),
    F(0, "Fail");

    private final int minPercentage;
    private final String remark;

    Grade(int minPercentage, String remark) {
        this.minPercentage = minPercentage;
        this.remark = remark;
    }

    public int getMinPercentage() {
        return minPercentage;
    }

    public String getRemark() {
        return remark;
    }

    /**
     * Classifies a percentage score into the appropriate Grade.
     */
    public static Grade fromPercentage(double percentage) {
        for (Grade grade : Grade.values()) {
            if (percentage >= grade.minPercentage) {
                return grade;
            }
        }
        return F;
    }
}
