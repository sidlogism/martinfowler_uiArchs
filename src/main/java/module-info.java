
module imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view {
    requires javafx.base;
    requires javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.fxml;
    requires org.json;
    requires com.zaxxer.hikari;
    requires java.sql;
    requires org.apache.logging.log4j;

    opens imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view to javafx.graphics;
    exports imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view;
}
