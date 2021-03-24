
module imperfectsilentart.martinfowler.uiArchs.formsandcontrols {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires org.json;

    
    opens imperfectsilentart.martinfowler.uiArchs.formsandcontrols to javafx.graphics;
    exports imperfectsilentart.martinfowler.uiArchs.formsandcontrols;
}
