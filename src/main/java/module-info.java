module imperfectsilentart.martinfowler.uiArchs.mvc_standalone {
	requires java.sql;
	requires jakarta.persistence;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.graphics;
	requires org.json;
	requires com.zaxxer.hikari;
	requires org.apache.logging.log4j;

	opens imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view to javafx.graphics;
	exports imperfectsilentart.martinfowler.uiArchs.mvc_standalone;
}
