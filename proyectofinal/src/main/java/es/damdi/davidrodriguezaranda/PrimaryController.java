package es.damdi.davidrodriguezaranda;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import com.google.firebase.database.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.awt.EventQueue;

public class PrimaryController {

    FirebaseDatabase db;

    @FXML
    private TableView<Employee> employeeTable;

    @FXML
    private TableColumn<Employee, String> emailColumn;

    @FXML
    private TableColumn<Employee, String> nameColumn;

    @FXML
    private TextField emailField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField passwordField;

    private ObservableList<Employee> employeeData = FXCollections.observableArrayList();

    public void initialize() throws IOException, InterruptedException {

        FileInputStream serviceAccount = new FileInputStream("key.json");

        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl("https://proyectofinal-29247-default-rtdb.europe-west1.firebasedatabase.app/")
            .build();

        FirebaseApp.initializeApp(options);

        db= FirebaseDatabase.getInstance();

        ArrayList<Camarero> empleados = new ArrayList<Camarero>();


        //*********************************************************************************************************************************************************************************************
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.getReference("camareros").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        
                        for (DataSnapshot i : snapshot.getChildren()) {
                            try {
                                Map<String, Object> data = (Map<String, Object>) i.getValue();
                                Camarero c = new Camarero();
                                c.setNombre((String) data.get("nombre"));
                                c.setEmail((String) data.get("email"));
                                c.setPassword((String) data.get("password"));
                                c.setOnline((Boolean) data.get("online"));
    
                                empleados.add(c);
                            }catch(DatabaseException e) {
                                System.err.println("Error al recuperar el camarero: " + e.getMessage());
                            }
                        }
                        // Update the UI on the EDT
                        EventQueue.invokeLater(new Runnable() {
                            public void run() {
                                // Update the UI here
                                emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
                                nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

                                for(Camarero e : empleados) {
                                    Employee emp= new Employee(e.getEmail(), e.getName(), e.getPassword(), e.getOnline());
                                    employeeData.add(emp);

                                }
                                employeeTable.setItems(employeeData);

                                employeeTable.getSelectionModel().selectedItemProperty().addListener(
                                        (observable, oldValue, newValue) -> showEmployeeDetails(newValue));
                            }
                        });
                    }
                
                    @Override
                    public void onCancelled(DatabaseError error) {
                        System.out.print("ERROR ERROR ERROR");
                    }
                });
            }
        }).start();        


        //*************************************************************************************************************************************************************************************************************
    }

    private void showEmployeeDetails(Employee employee) {
        if (employee != null) {
            emailField.setText(employee.getEmail());
            nameField.setText(employee.getName());
            passwordField.setText(employee.getPassword());
        }else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("Selecciona un empleado por favor.");
            alert.showAndWait();

        }

    }

    @FXML
    private void saveEmployee() {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();

        if (selectedEmployee != null) {
            selectedEmployee.setEmail(emailField.getText());
            selectedEmployee.setName(nameField.getText());
            selectedEmployee.setPassword(passwordField.getText());

            // refresh the table view to reflect the changes
            employeeTable.refresh();
        }
    }

}
