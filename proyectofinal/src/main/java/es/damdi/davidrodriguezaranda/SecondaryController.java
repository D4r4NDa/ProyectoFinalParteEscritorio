package es.damdi.davidrodriguezaranda;

import java.io.FileInputStream;
import java.io.IOException;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class SecondaryController {

  FirebaseAuth auth;

  @FXML
  private TextField nameField;

  @FXML
  private TextField emailField;

  @FXML
  private PasswordField passwordField;

  @FXML
  private Button registerButton;

  @FXML
  private void handleRegisterButton(ActionEvent event) {
      createUser();
  }

    public void createUser() {

      FileInputStream serviceAccount = null;
    try {
      serviceAccount = new FileInputStream("key.json");
      FirebaseOptions options = FirebaseOptions.builder()
          .setCredentials(GoogleCredentials.fromStream(serviceAccount))
          .build();
      FirebaseApp.initializeApp(options);
    } catch (IOException e) {
      System.err.println("Error initializing Firebase app: " + e.getMessage());
    } finally {
      if (serviceAccount != null) {
        try {
          serviceAccount.close();
        } catch (IOException e) {
          System.err.println("Error closing service account file: " + e.getMessage());
        }
      }
    }

        // Initialize Firebase app
        auth = FirebaseAuth.getInstance();
    
        String email;
        String password;
        String name;
        String uRecord="";

        email= emailField.getText();
        password= passwordField.getText();
        name= nameField.getText();
    
        try {
          CreateRequest request = new CreateRequest()
              .setEmail(email)
              .setEmailVerified(false)
              .setPassword(password);
          UserRecord userRecord = auth.createUser(request);
          uRecord= userRecord.getUid();
          Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Employee Registration");
            Label dialogLabel = new Label("Successfully registered employee " + name + " with email " + email);
            dialogLabel.setFont(new Font(16));
            Button dialogButton = new Button("OK");
            dialogButton.setOnAction(e -> dialog.close());
            VBox dialogVbox = new VBox(dialogLabel, dialogButton);
            dialogVbox.setAlignment(javafx.geometry.Pos.CENTER);
            Scene dialogScene = new Scene(dialogVbox, 400, 200);
            dialog.setScene(dialogScene);
            dialog.show();
        } catch (FirebaseAuthException ex) {
          Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Employee Registration");
            Label dialogLabel = new Label("Error creando el usuario");
            dialogLabel.setFont(new Font(16));
            Button dialogButton = new Button("OK");
            dialogButton.setOnAction(e -> dialog.close());
            VBox dialogVbox = new VBox(dialogLabel, dialogButton);
            dialogVbox.setAlignment(javafx.geometry.Pos.CENTER);
            Scene dialogScene = new Scene(dialogVbox, 400, 200);
            dialog.setScene(dialogScene);
            dialog.show();
        }
    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}