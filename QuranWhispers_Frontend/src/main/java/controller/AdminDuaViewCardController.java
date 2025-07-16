package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.json.JSONObject;
import util.BackendAPI;
import util.GlobalState;

import java.io.IOException;

public class AdminDuaViewCardController extends BaseControllerAdmin {
    @FXML Text duaTitle;
    @FXML Text duaArabicBody;
    @FXML Text duaEnglishBody;

    public void setupAdminDuaViewInfo(String title, String arabicBody, String englishBody) {
        duaTitle.setText(title);
        duaArabicBody.setText(arabicBody);
        duaEnglishBody.setText(englishBody);
    }

    public void handleDeleteBtn(MouseEvent e) throws IOException {
        System.out.println("Delete Btn Pressed");
        playClickSound();
        Task<Void> deleteDuaBackendAPITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("title", duaTitle.getText());

                JSONObject response = BackendAPI.fetch("deletedua", request);
                if (response.getString("status").equals("200")) {
                    System.out.println("Dua Deleted successfully: " + response.toString());
                    Platform.runLater(() -> {
                        try {
                            if (sceneController != null) {
                                AdminDuaViewController adminDuaViewController = (AdminDuaViewController) sceneController.switchTo(GlobalState.ADMIN_DUA_VIEW_FILE);
                                adminDuaViewController.setupDuaViewTable();
                            } else {
                                System.err.println("sceneController is null in AdminDuaViewCardController.");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                return null;
            }
        };
        new Thread(deleteDuaBackendAPITask).start();
    }
}
