module imperfectsilentart.martinfowler.uiArchs.mvc_standalone {
	requires javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.graphics;
	requires org.json;
	requires com.zaxxer.hikari;
	requires org.apache.logging.log4j;
	requires java.sql;
	requires java.persistence;
	// for hibernate
	requires org.hibernate.orm.core;
	requires net.bytebuddy;
	requires com.fasterxml.classmate;
	requires java.xml.bind;

	opens imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view to javafx.graphics;
	exports imperfectsilentart.martinfowler.uiArchs.mvc_standalone;
	opens imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence to org.hibernate.orm.core;
	exports imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence;
}
