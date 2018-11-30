package main.java.org.javafx.bumperui.application;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application{

	private static Stage prStage;
	private static Scene scene;
	private static Controller c;

	private double xOffset = 0;
	private double yOffset = 0;

	@Override
	public void start(Stage primaryStage) throws Exception {

		//A new controller instance is associated with each call to FXMLLoader.load(...) aka loader.load() your Controller class elements can't be static
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainController.fxml"));
		Parent root = (Parent) loader.load();

		//Make window movable by pressing anywhere
		root.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = event.getSceneX();
				yOffset = event.getSceneY();
			}
		});

		//move around here
		root.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				primaryStage.setX(event.getScreenX() - xOffset);
				primaryStage.setY(event.getScreenY() - yOffset);
			}
		});



		primaryStage.setTitle("BumperTool");

		scene = new Scene(root, 600, 420);//  ;)

		// Transparent scene and stage
		scene.setFill(Color.TRANSPARENT);
		primaryStage.initStyle(StageStyle.UNDECORATED);

		primaryStage.setScene(scene);
		primaryStage.show();


		//WE USE FXMLLoader.getController() to get a reference to our Controller object since JavaFX doesn't allow any field to be static
		c = loader.getController();
		prStage = primaryStage;

	}

	public static void main(String[] args) {
	    launch(args);
	}

	public static Stage getPrStage(){
		return prStage;
	}

	public static Controller getControllerRef(){return c;}

	public static Scene getScene() {
		return scene;
	}

}
