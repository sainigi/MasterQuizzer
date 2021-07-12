package com.example.quizzer;

public class QuestionModel {

    private String question,optionA,optionB,optionC,optionD,correctOption;
    private int setNo;

    public QuestionModel(){
    }

    public QuestionModel(String question, String optionA, String optionB, String optionC, String optionD, String correctOption,int setNo) {
        this.setNo = setNo;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
    }

    public int getSetNo() {
        return setNo;
    }

    public void setSetNo(int setNo) {
        this.setNo = setNo;
    }

    public String getQuestion() {
        return question;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public void setCorrectOption(String correctOption) {
        this.correctOption = correctOption;
    }
}
