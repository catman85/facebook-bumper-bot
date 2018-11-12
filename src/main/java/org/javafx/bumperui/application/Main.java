package main.java.org.javafx.bumperui.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application{

	private static Stage prStage;
	private static Controller c;

	@Override
	public void start(Stage primaryStage) throws Exception {

		//A new controller instance is associated with each call to FXMLLoader.load(...) aka loader.load() your Controller class elements cant be static
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainController.fxml"));
		Parent root = (Parent) loader.load();

		//WE USE FXMLLoader.getController() to get a reference to our Controller object since JavaFX doesn't allow any field to be static
		c = loader.getController();

		primaryStage.setTitle("BumperTool");
		primaryStage.setScene(new Scene(root, 600, 400));
		primaryStage.show();
		prStage = primaryStage;

	}

	public static void main(String[] args) {
	    launch(args);
	}

	public static Stage getPrStage(){
		return prStage;
	}

	public static Controller getControllerRef(){return c;}

}
