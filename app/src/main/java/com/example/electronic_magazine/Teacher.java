package com.example.electronic_magazine;

public class Teacher {
    private String fullName;
    private String subject;

    public Teacher() {}

    public Teacher(String fullName, String subject) {
        this.fullName = fullName;
        this.subject = subject;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
