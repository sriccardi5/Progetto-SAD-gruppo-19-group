module it.unisa.progettosadgruppo19 {
    requires javafx.controls;
    requires javafx.fxml;

    opens it.unisa.progettosadgruppo19.controller to javafx.fxml;
    exports it.unisa.progettosadgruppo19;
}
