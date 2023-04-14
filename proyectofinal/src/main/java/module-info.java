module es.damdi.davidrodriguezaranda {
    requires javafx.controls;
    requires javafx.fxml;
    requires firebase.admin;
    requires com.google.auth.oauth2;

    opens es.damdi.davidrodriguezaranda to javafx.fxml;
    exports es.damdi.davidrodriguezaranda;
}
