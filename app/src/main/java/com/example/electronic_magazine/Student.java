package com.example.electronic_magazine;

import java.util.List;

public class Student {
    private String fullName;
    private String schoolClass;

    public Student() {}

    public Student(String fullName, String schoolClass) {
        this.fullName = fullName;
        this.schoolClass = schoolClass;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSchoolClass() {
        return schoolClass;
    }

    public void setSchoolClass(String schoolClass) {
        this.schoolClass = schoolClass;
    }
}
