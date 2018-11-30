package main.java.org.javafx.bumperui.application;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.java.org.javafx.bumperui.data.Account;
import main.java.org.javafx.bumperui.data.PostsToBump;
import org.apache.commons.lang.StringUtils;
import org.controlsfx.control.Notifications;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class Controller {

    //these can't be static JavaFX
    @FXML public JFXTextField url;
    @FXML public JFXButton go;
    @FXML public JFXTextField path;
    @FXML public JFXTextField commentJFX;
    @FXML public JFXTextField sleepTimeJFX;
    @FXML public JFXTextField emailJFX;
    @FXML public JFXPasswordField passJFX;
    @FXML public JFXDatePicker dateJFX;
    @FXML public Label counter;

    private static Stage prStage = Main.getPrStage();
    private static File file;
    private ArrayList<String> posts;

    @FXML
    private void initialize(){
        // Listen for text changes in the JFXTextField "path" variable
        path.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                importPostsFromFile();
            }
        });
    }

    //Shows Sleep Tip
    @FXML
    public void showSleepTip(){
        Notifications.create().darkStyle().hideAfter(Duration.seconds(2))
                .title("Tip")
                .text("A good value for Sleep Time is > 1700 ms")
                .showInformation();
    }

    //File Explorer button
    @FXML
    public void explore(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Text File With your Pemalinks");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        //showOpenDialog
        file = fileChooser.showOpenDialog(prStage);

        if (file != null) {
            System.out.println(file.toString());
            path.setText(file.toString());
        }


    }



    //Transfers post links from a file to an ArrayList
    private void importPostsFromFile(){
        posts = new ArrayList<String>();

        file = new File(path.getText());

        if (file == null) {
            Notifications.create().darkStyle().hideAfter(Duration.seconds(2))
                    .title("Error")
                    .text("Please Select a file with your Permalinks")
                    .showError();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(this.file))) {
            String line;
            //stays as long as next line is not null and it is not empty
            while ((line = br.readLine())!=null && !line.trim().isEmpty() ) {
                //exists if next line is null || if next line is empty

                System.out.print(line + " ");
                System.out.println(toMobile(line));

                //Convert url to mobile
                //Add post link to the posts ArrayList
                posts.add(toMobile(line));
            }

            this.counter.setText(String.valueOf(this.posts.size()));
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            System.out.println(posts.size());
        }
    }

    //converts reqular facebook links to mobile facebook
    private String toMobile(String s){
        return s.replaceAll("www.", "m.");
    }


    private boolean isAnyObjectNull(Object... objects) {
        for (Object o: objects) {
            if (o == null) {
                return true;
            }
        }
        return false;
    }

    private boolean validated(){
        if(isAnyObjectNull(posts, commentJFX.getText(), sleepTimeJFX.getText(),emailJFX.getText(), passJFX.getText(), dateJFX.getValue(),path)){
            Notifications.create().darkStyle().hideAfter(Duration.seconds(2))
                    .title("Error")
                    .text("Missing arguments")
                    .showError();
            return false;
        }
        if(!StringUtils.isNumeric(sleepTimeJFX.getText())){
            Notifications.create().darkStyle().hideAfter(Duration.seconds(2))
                    .title("Error")
                    .text("Sleep Time must be a numeric value")
                    .showError();
            return false;
        }

        return true;
    }

    @FXML
    public void minimize(){
        Main.getPrStage().setIconified(true);
    }


    @FXML
    public void exit(){
        Platform.exit();
        System.exit(0);
        return;
    }

    //Final Submit button
    public void bump(ActionEvent actionEvent) {
	    //Make sure you have proper input
//        importPostsFromFile();
        if(!validated()){return;}

        Notifications.create().darkStyle().hideAfter(Duration.seconds(2))
                .title("Bumping Starts")
                .text("The browser will open up")
                .showConfirm();

        //Here you grab the stuff from the UI by running the myThread() constructor
        myThread bum = new myThread();

        //Here you make a new Bumper
        bum.start();

    }


    public void exportSet(ActionEvent event){
        importPostsFromFile();
        if(!validated()){return;}

        ieSettings ie = new ieSettings("e");

        ie.start();
    }

    public void importSet(ActionEvent event){
        ieSettings ie = new ieSettings("i");

        ie.start();
    }


    public class myThread implements Runnable {

        private PostsToBump postsToBump;
        private Account account;
        private Thread t;

        public myThread() {

            LocalDate selectedDate = dateJFX.getValue();
            String year = String.valueOf(selectedDate.getYear());
            String month = StringUtils.capitalize(selectedDate.getMonth().toString().toLowerCase());
            Integer day = selectedDate.getDayOfMonth();

            System.out.println("Selected Date: " + year + month + day.toString());

            //relative path from root directory of the project or full path
            this.postsToBump = new PostsToBump(posts,commentJFX.getText(),new Long(sleepTimeJFX.getText()));
            this.account = new Account(emailJFX.getText(),passJFX.getText(),year,month,day);

        }

        //this method will trigger the run method below
        public void start () {
            System.out.println("Starting Thread... ***");
            if (t == null) {
                t = new Thread(this, "Bumper Thread");
                t.start ();
            }
        }

        @Override
        public void run() {
            new Bumper(postsToBump, account);
        }

    }
}
