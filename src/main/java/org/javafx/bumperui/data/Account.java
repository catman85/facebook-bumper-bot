package main.java.org.javafx.bumperui.data;

public class Account {
    private String email;
    private String password;

    //Bday
    private String year;
    private String month;
    private Integer day;

    public Account(String email, String password, String year, String month, Integer day) {
        this.email = email;
        this.password = password;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public Integer getDay() {
        return day;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public void showInfo(){
        System.out.println(email + " " + password + " " + year + " " + month + " " + day);
    }
}
