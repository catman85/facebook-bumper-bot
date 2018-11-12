package main.java.org.javafx.bumperui.data;

import main.java.org.javafx.bumperui.application.Main;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.ArrayList;

public class PostsToBump {

    private ArrayList<String> posts;
    private ArrayList<String> postsCopy;
    private String comment;
    private Long sleepTime;
    private Label counter;


    public Long getSleepTime() {
        return sleepTime;
    }

    public PostsToBump(ArrayList<String> posts, String comment, Long sleepTime) {
        this.posts=posts;

        //copy by value
        this.postsCopy= new ArrayList<>(posts);
        //copy by reference
        //this.postsCopy = posts;
        this.comment = comment;
        this.sleepTime = sleepTime;
        this.counter = counter;
    }

    public ArrayList<String> getPosts() {
        return posts;
    }

    public String getComment() {
        return comment;
    }

    public void removePost(){
        this.posts.remove(this.posts.size()-1);
        updateUI();
    }

    public void ressurectPosts(){
        posts = postsCopy;
        updateUI();
    }

    private void updateUI(){
        //The user interface cannot be directly updated from a non-application thread. Instead, use Platform.runLater()
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // Update UI here.
                Main.getControllerRef().counter.setText(String.valueOf(posts.size()));
            }
        });
    }

    public Integer getPostsSize(){
        return this.posts.size();
    }
}
