package main.java.org.javafx.bumperui.application;

import main.java.org.javafx.bumperui.data.Account;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ieSettings implements Runnable {

    private Long sleep;
    private String comment;
    private String path;
    private Account account;

    private Thread th;
    private static Controller c;
    private String option;
    private JSONObject obj;

    public ieSettings(String ie) {
        c = Main.getControllerRef();
        option = ie;
    }

    //this method will trigger the run method below
    public void start(){
        System.out.println("Starting Thread... ***");
        if (th == null) {
            th = new Thread(this, "ieSettings Thread");
            th.start ();
        }
    }

    @Override
    public void run() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(option.equals("e")){
                    getDataFromUI();
                    bindDataToJson();
                    writeDataToFile();
                }else if(option.equals("i")){
                    getDataFromFile();
                    bindDataFromJson();
                    putDataInTheUi();
                }
            }
        });
    }


    private void getDataFromUI(){
        //Account
            //An Array of 4 strings and an Integer
        String mail = String.valueOf(c.emailJFX.getText());
        String pass = String.valueOf(c.passJFX.getText());
        LocalDate selectedDate = c.dateJFX.getValue();
        String year = String.valueOf(selectedDate.getYear());
        String month = StringUtils.capitalize(selectedDate.getMonth().toString().toLowerCase());
        Integer day = selectedDate.getDayOfMonth();
        account = new Account(mail,pass,year,month,day);

        //PostsToBump
            //An Array of 1 Long and 2 Strings
        sleep = new Long(c.sleepTimeJFX.getText());
        comment = String.valueOf(c.commentJFX.getText());
        path = String.valueOf(c.path.getText());
    }

    private void bindDataToJson(){
        obj = new JSONObject();
        obj.put("Bumper", "Settings");


        JSONArray bumps = new JSONArray();
            JSONObject sleep = new JSONObject();
                sleep.put("SleepTime",this.sleep);
                bumps.add(sleep);
            JSONObject comment = new JSONObject();
                comment.put("Comment",this.comment);
                bumps.add(comment);
            JSONObject path = new JSONObject();
                path.put("Path",this.path);
                bumps.add(path);
        obj.put("Bumps", bumps);


        JSONArray account = new JSONArray();
            JSONObject mail = new JSONObject();
                mail.put("Mail",this.account.getEmail());
                account.add(mail);
            JSONObject pass = new JSONObject();
                pass.put("Password",this.account.getPassword());
                account.add(pass);
            JSONObject year = new JSONObject();
                year.put("Year",this.account.getYear());
                account.add(year);
            JSONObject month = new JSONObject();
                month.put("Month",this.account.getMonth());
                account.add(month);
            JSONObject day = new JSONObject();
                day.put("Day",this.account.getDay());
                account.add(day);
        obj.put("Account",account);

    }

    private void writeDataToFile(){

                //Select File
                FileChooser chooser = new FileChooser();
                //.bump files
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Bumper file (*.json)","*.json");
                chooser.getExtensionFilters().add(extFilter);
                chooser.setTitle("Select Directory to Save your Settings");
                chooser.setInitialDirectory(
                        new File(System.getProperty("user.home"))
                );
                //showSaveDialog
                File file = chooser.showSaveDialog(Main.getPrStage());

                //Write Data to file
                if(file!=null){
                    try {
                        FileWriter fw = new FileWriter(file);
                        //Write our JSONObject to the selected file
                        fw.write(obj.toJSONString());
                        System.out.println("Successfully Copied JSON Object to File...");
                        System.out.println("\nJSON Object: " + obj);
                        fw.flush();
                        fw.close();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
    }




    private void getDataFromFile() {
                //Select File
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Bumper file (*.json)", "*.json");
                fileChooser.getExtensionFilters().add(extFilter);
                fileChooser.setTitle("Select File With your Settings");
                fileChooser.setInitialDirectory(
                        new File(System.getProperty("user.home"))
                );
                //showOpenDialog
                File file = fileChooser.showOpenDialog(Main.getPrStage());

                //Get Data from file
                if (file != null) {
                    try {
                        JSONParser parser = new JSONParser();
                        FileReader fr = new FileReader(file);
                        Object object = parser.parse(fr);
                        obj = (JSONObject) object;
                        System.out.println("Successfully Imported JSON Object from File...");
                        System.out.println("\nJSON Object: " + obj);

                        fr.close();
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                }
    }


    private void bindDataFromJson(){
        System.out.println("Importing... ***");
        System.out.println((String) obj.get("Bumper"));
        JSONArray bumps = (JSONArray) obj.get("Bumps");
        JSONArray account = (JSONArray) obj.get("Account");
        JSONObject t;

        //Bumps
        t = (JSONObject)bumps.get(0);
        this.sleep = (Long) t.get("SleepTime");

        t = (JSONObject)bumps.get(1);
        this.comment = (String) t.get("Comment");

        t = (JSONObject)bumps.get(2);
        this.path = (String) t.get("Path");



        //Account
        this.account = new Account("","","","",1);

        t = (JSONObject)account.get(0);
        this.account.setEmail((String)t.get("Mail"));

        t = (JSONObject)account.get(1);
        this.account.setPassword((String)t.get("Password"));

        t = (JSONObject)account.get(2);
        this.account.setYear((String)t.get("Year"));

        t = (JSONObject)account.get(3);
        this.account.setMonth((String)t.get("Month"));

        t = (JSONObject)account.get(4);
        this.account.setDay((int)(long) t.get("Day"));

        this.account.showInfo();

        System.out.println("Results" + sleep + comment + path);

    }

    private void putDataInTheUi(){
        c.emailJFX.setText(account.getEmail());
        c.passJFX.setText(account.getPassword());


        int year=Integer.parseInt(this.account.getYear());
        int month= 0;
        try {
            month = monthNameToNum(this.account.getMonth())+1;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        int day=this.account.getDay();


        c.dateJFX.setValue(LocalDate.of(year,month,day));

        c.path.setText(path);
        c.sleepTimeJFX.setText(sleep.toString());
        c.commentJFX.setText(comment);
    }

    private int monthNameToNum(String mon) throws java.text.ParseException {
        int month = 0;
        try {
            Date date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(mon);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            month = cal.get(Calendar.MONTH);
            return month;
        }catch (java.text.ParseException e){
            e.printStackTrace();
        }finally {
            return month;
        }
    }
}


