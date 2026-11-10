module co.edu.uniquindio.panaderia {
    requires javafx.controls;
    requires javafx.fxml;

    opens co.edu.uniquindio.panaderia to javafx.fxml;
    opens co.edu.uniquindio.panaderia.viewcontrollers to javafx.fxml;

    exports co.edu.uniquindio.panaderia;
    exports co.edu.uniquindio.panaderia.model;
    exports co.edu.uniquindio.panaderia.controllers;
    exports co.edu.uniquindio.panaderia.viewcontrollers;
}