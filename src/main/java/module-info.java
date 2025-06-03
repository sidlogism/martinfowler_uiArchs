// TODO Check if separating subprojects formsandcontrols and mvc_standalone in two modules helps clarity and prevents using classes from wrong subproject.
module imperfectsilentart.martinfowler.uiArchs.mvc_standalone {
	requires javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.graphics;
	requires org.json;
	requires com.zaxxer.hikari;
	requires org.apache.logging.log4j;
	requires java.sql;
	requires jakarta.persistence;
	// for hibernate
	requires org.hibernate.orm.core;
	//requires net.bytebuddy;
	requires com.fasterxml.classmate;
	requires jakarta.xml.bind;

	opens imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view to javafx.fxml;
	opens imperfectsilentart.martinfowler.uiArchs.mvc_standalone.controller to javafx.graphics,javafx.fxml;
	opens imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model to org.hibernate.orm.core;
	opens imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence to org.hibernate.orm.core;
	opens imperfectsilentart.martinfowler.uiArchs.model2_passive_view.model.persistence to org.hibernate.orm.core;
}
