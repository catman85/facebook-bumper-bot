package main.java.org.javafx.bumperui.application;

//import junit.framework.Assert;

import javafx.application.Platform;
import javafx.util.Duration;
import main.java.org.javafx.bumperui.data.Account;
import main.java.org.javafx.bumperui.data.PostsToBump;
import main.java.org.javafx.bumperui.tools.JavaFXTools;
import main.java.org.javafx.bumperui.tools.NotificationType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

//import org.junit.Test;
//TODO junit test

public class Bumper {

    private WebDriver driver;

    private PostsToBump postsToBump;
    private Account account;


    public Bumper(PostsToBump postsToBump, Account account) {
        this.postsToBump=postsToBump;
        this.account=account;

        //With the Following commands we adding the geckodriver.exe to PATH see: https://firefox-source-docs.mozilla.org/testing/geckodriver/geckodriver/Usage.html
        //? The Program is working even if the path in the next line is wrong
        //        File gecko = new File("C:\\Users\\Jim\\Desktop\\Programming\\Java\\BumperUI\\geckodriver.exe");
        //        System.setProperty("webdriver.gecko.driver", gecko.getAbsolutePath());

        System.setProperty("webdriver.gecko.driver", "resources/geckodriver.exe");
        driver = new FirefoxDriver();
        startWebDriver();
    }

    public void startWebDriver(){
        //--Visit FB and Login
        loginFb();

        confirmation();

        while(this.postsToBump.getPostsSize()>0) {
            //--Goes to your post
            goToPost();

            //--Bump dat lil bitch
            bump();

            //--Remove the post you just visited and update the UI
            removePost();
        }

        // ^_^
        success();

        //--close the browser
        driver.quit();
        //        driver.close(); geckodriver remains in the task manager
    }


    private void loginFb(){
        //Go to m.facebook.com
        driver.navigate().to("https://m.facebook.com");
        //make sure it's all good
//        Assert.assertTrue("Title should start differently!!", driver.getTitle().startsWith("Facebook"));

        //Type Username and Pass
        driver.findElement(By.xpath("//*[@id=\"m_login_email\"]")).sendKeys(this.account.getEmail());
        driver.findElement(By.xpath("/html/body/div/div/div[2]/div/table/tbody/tr/td/div[2]/div[2]/form/ul/li[2]/div/input")).sendKeys(this.account.getPassword());
        //Login
        driver.findElement(By.xpath("/html/body/div/div/div[2]/div/table/tbody/tr/td/div[2]/div[2]/form/ul/li[3]/input")).click();
        try {
            //Not Now
            driver.findElement(By.xpath("/html/body/div/div/div/div/table/tbody/tr/td/div/div[3]/a")).click();
        }catch(org.openqa.selenium.NoSuchElementException e){
            e.printStackTrace();
        }
    }

    private void confirmation(){
        try {

            driver.findElement(By.xpath("//*[@id=\"checkpointSubmitButton-actual-button\"]")).click();
            driver.findElement(By.xpath("//*[@id=\"checkpointSubmitButton-actual-button\"]")).click();

            System.out.println("Validating...");

            //May 14 1993
            Select month = new Select(driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div/div/div[2]/div[3]/div[1]/select")));
            Select day = new Select(driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div/div/div[2]/div[3]/div[2]/select")));
            Select year = new Select(driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div/div/div[2]/div[3]/div[3]/select")));

            //Grab info from the account object
            month.selectByVisibleText(this.account.getMonth());
            day.selectByIndex(this.account.getDay()-1);
            year.selectByVisibleText(this.account.getYear());

            //submit bday
            driver.findElement(By.xpath("//*[@id=\"checkpointSubmitButton-actual-button\"]")).click();
            //You're All set!
            driver.findElement(By.xpath("//*[@id=\"checkpointSubmitButton-actual-button\"]")).click();
        }catch(org.openqa.selenium.NoSuchElementException e){
            System.out.println("Already Validated");
        }

    }

    //goes to given links
    private void goToPost(){
        //go to the post
        driver.navigate().to(postsToBump.getPosts().get(postsToBump.getPosts().size()-1));

        System.out.println("Removing post from Array..."+ postsToBump.getPostsSize());
    }

    //bumps post
    private void bump(){
        try {
            //the following process fails through the tor browser for some reason
            //you loose access to your posts

            //click on the comment section
            driver.findElement(By.xpath("//*[@id=\"composerInput\"]")).click();

            //type comment
            driver.findElement(By.xpath("//*[@id=\"composerInput\"]")).sendKeys(postsToBump.getComment());

            //hit Enter
//        driver.findElement(By.xpath("//*[@id=\"composerInput\"]")).sendKeys(Keys.RETURN);

            // hit Comment button if someone made a comment it doesn't work
//        driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div[1]/div[2]/div/div[5]/form[1]/table/tbody/tr/td[2]/div/input")).click();

            //changes per post
//        driver.findElement(By.className("ef")).click();

            //works only with first
//        driver.findElement(By.cssSelector(".eg")).click();

            //works
            driver.findElement(By.xpath("//input[@value='Comment']")).click();

            //wait a little bit
            //makes the comment look more realistic
            try {
                Thread.sleep(postsToBump.getSleepTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //wait for the comment to be published before searching for "Edit"
            driver.manage().timeouts().implicitlyWait(postsToBump.getSleepTime(), TimeUnit.MILLISECONDS);

            //Try finding the Edit Comment you just made
            findEditAndClick();

            //delete
            driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div[1]/div/a")).click();
            //Are you sure? yes
            driver.findElement(By.xpath("/html/body/div/div/div[2]/div[2]/div[1]/form/table/tbody/tr/td[2]/input")).click();

        }catch (org.openqa.selenium.NoSuchElementException e){
            //if you loose access to a post just keep on doing the rest
            e.printStackTrace();
        }

    }


    private void findEditAndClick(){
        //this first try is executed when you use a different account
        //not the one that made the actual post
        try{
            driver.findElement(By.linkText("Edit")).click();//Click Edit
            return;
        }catch(NoSuchElementException a){
            System.out.println("!!Method 1: Link Text failed!!");
        }


        try{
            driver.findElement(By.cssSelector(".cx > a:nth-child(5)\n")).click();//Click Edit
            return;
        }catch(NoSuchElementException b){
            System.out.println("!!Method 2: CSS Selector failed!!");
        }


        try{
            driver.findElement(By.cssSelector("div.bt:nth-child(4) > a:nth-child(5)")).click();//Click Edit
            return;
        }catch(NoSuchElementException c){
            System.out.println("!!Method 3: CSS Selector failed!!");
            c.printStackTrace();
        }
    }


    private void removePost(){
        //remove the link you just visited from the array
        //TODO bug: the comment was not deleted in the last post in the permalinks text file
        postsToBump.removePost();
    }

    private void success(){

        //prepare for next Bump Round
        postsToBump.ressurectPosts();


        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // Update UI here.
                JavaFXTools.showNotification("Success","Success", Duration.seconds(1), NotificationType.SUCCESS);

            }
        });

        try {
            Thread.sleep(postsToBump.getSleepTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

}
