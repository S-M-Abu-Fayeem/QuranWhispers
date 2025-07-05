package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class AdminInsertController extends BaseControllerAdmin {
    @FXML
    TextField duaTitleField;
    @FXML TextField duaArabicBodyField;
    @FXML TextField duaEnglishBodyField;
    @FXML TextField surahNumField;
    @FXML TextField ayahNumField;
    @FXML TextField emotionField;
    @FXML TextField themeField;

    public void handleDuaSubmitBtn(MouseEvent e) throws IOException {
        System.out.println("Dua Submit Button Pressed");
        playClickSound();
    }

    public void handleVerseSubmitBtn(MouseEvent e) throws IOException {
        System.out.println("Verse Submit Button Pressed");
        playClickSound();
    }
}
