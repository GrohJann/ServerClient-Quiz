package model;


public class Questions {

    private String[][] questions;

    public Questions(){
        questions = new String[][] {
                {"Wie gross ist Shundes Schugroese?", "4", "B", "41","41"},
                {"Wie gross ist Shundes Schugroese?", "4", "B", "41","41"},
                {"Wie gross ist Shundes Schugroese?", "4", "B", "41","41"},
                {"Wie gross ist Shundes Schugroese?", "4", "B", "41","41"},
                {"Wie gross ist Shundes Schugroese?", "4", "B", "41","41"},
                {"Wie gross ist Shundes Schugroese?", "4", "B", "41","41"},
                {"Wie gross ist Shundes Schugroese?", "4", "B", "41","41"},
                {"Wie gross ist Shundes Schugroese?", "4", "B", "41","41"},
                {"Wie gross ist Shundes Schugroese?", "4", "B", "41","41"},
                {"Wie gross ist Shundes Schugroese?", "4", "B", "41","41"},
        };
    }

    public String[] getRandQuestion(){
        return questions[(int)(Math.random() * (questions.length-1) + 1)];
    }
}
