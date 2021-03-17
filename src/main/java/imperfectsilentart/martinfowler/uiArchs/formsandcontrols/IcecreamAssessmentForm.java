package imperfectsilentart.martinfowler.uiArchs.formsandcontrols;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * "Forms and Controls" version of assessment form from https://www.martinfowler.com/eaaDev/uiArchs.html .
 */
public class IcecreamAssessmentForm extends Application
{
	@Override
	public void start(final Stage stage) throws Exception
	{
		final HBox rootPane = new HBox(10);
		rootPane.setPadding(new Insets(7,7,7,7));
		rootPane.setAlignment(Pos.BASELINE_LEFT);
		
		/*
		 * Scrollable list of monitoring station.
		 * On change of selected station in list, the value of the text field "Station ID" is updated.
		 * On change of the text field "Station ID", the selected station in the list is updated as well.
		 */
		final ListView<String> stationList = new ListView<String>();
		
		/*
		 * This pane is an interface for showing, searching and modifying ice cream reading records.
		 * The pane contains all data fields (read only) and record entries of one ice cream reading record.
		 * Each field or entry has a label with corresponding text field.
		 * 
		 * The record entries "Station ID" and "Date" can be modified to initiate an internal search for the corresponding ice cream reading record.
		 * The record entry "Actual" can be modified to change the actual value of the currently active ice cream reading record.
		 * All other entries and data fields are calculated from external sources and thus the corresponding text fields are read only.
		 */
		final GridPane icReadingFields = new GridPane();
		icReadingFields.setPadding(new Insets(10,10,10,10));
		icReadingFields.setHgap(7);
		icReadingFields.setVgap(7);
		
		final Label lblStadionExternalId = new Label("Station ID");
		icReadingFields.add(lblStadionExternalId, 0, 0);
		icReadingFields.setHalignment(lblStadionExternalId, HPos.LEFT);
		final TextField tfStationExternalId = new TextField();
		icReadingFields.add(tfStationExternalId, 1, 0);
		
		final Label lblDate = new Label("Date");
		icReadingFields.add(lblDate, 0, 1);
		icReadingFields.setHalignment(lblDate, HPos.LEFT);
		final TextField tfDate = new TextField();
		icReadingFields.add(tfDate, 1, 1);
		
		final Label lblTargetConcentration = new Label("Target");
		icReadingFields.add(lblTargetConcentration, 0, 2);
		icReadingFields.setHalignment(lblTargetConcentration, HPos.LEFT);
		final TextField tfTargetConcentration = new TextField();
		icReadingFields.add(tfTargetConcentration, 1, 2);
		
		final Label lblActualConcentration = new Label("Actual");
		icReadingFields.add(lblActualConcentration, 0, 3);
		icReadingFields.setHalignment(lblActualConcentration, HPos.LEFT);
		final TextField tfActualConcentration = new TextField();
		icReadingFields.add(tfActualConcentration, 1, 3);
		
		final Label lblVariance = new Label("Variance");
		icReadingFields.add(lblVariance, 0, 4);
		icReadingFields.setHalignment(lblVariance, HPos.LEFT);
		final TextField tfVariance = new TextField();
		icReadingFields.add(tfVariance, 1, 4);
		
		/*
		 * Add listener for changes of selected station in the list.
		 */
		stationList.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<String>() {
					public void changed(ObservableValue<? extends String> ov, 
						String old_val, String new_val) {
						tfStationExternalId.setText(new_val);
				}
			});
		// TODO sample data
		final ObservableList<String> stationListData = FXCollections.observableArrayList(
				"chocolate", "salmon", "gold", "coral", "darkorchid",
				"darkgoldenrod", "lightsalmon", "black", "rosybrown", "blue",
				"blueviolet", "brown");
		stationList.setItems(stationListData);

		/*
		 * Create new scene consisting of two elements and link the scene to the stage.
		 * Leftmost element: a list of monitoring stations
		 * Rightmost element: fields & entries of an ice cream reading record
		 */
		rootPane.getChildren().addAll(stationList, icReadingFields);
		HBox.setHgrow(icReadingFields, Priority.ALWAYS);
		stage.setScene(new Scene(rootPane, 470, 200));
		// set stage attributes
		stage.setTitle("Assessment Record (\"Forms and Controls\" version)");
		stage.setResizable(true);  
		stage.centerOnScreen();
		// make stage visible
		stage.show();
	}

	public static void main(final String[] args)
	{
		launch(args);
	}
}
