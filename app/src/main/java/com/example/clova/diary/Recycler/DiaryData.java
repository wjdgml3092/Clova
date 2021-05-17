package com.example.clova.diary.Recycler;

public class DiaryData {

    private String date, title, word1, word2, word3;

    public DiaryData(String date, String title, String word1, String word2, String word3){
        this.date = date;
        this.title = title;
        this.word1 = word1;
        this.word2 = word2;
        this.word3 = word3;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWord1() {
        return word1;
    }

    public void setWord1(String word1) {
        this.word1 = word1;
    }

    public String getWord2() {
        return word2;
    }

    public void setWord2(String word2) {
        this.word2 = word2;
    }

    public String getWord3() {
        return word3;
    }

    public void setWord3(String word3) {
        this.word3 = word3;
    }
}


