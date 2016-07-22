package fr.ycoupe.pronobike.models;

/**
 * Created by Yann on 22/02/16.
 */
public class RankGame {

    private int userId;
    private String firstname;
    private String lastname;
    private int first;
    private int second;
    private int third;
    private int total;
    private int position;

    public RankGame(){

        this.userId = 0;
        this.firstname = "";
        this.lastname = "";
        this.first = 0;
        this.second = 0;
        this.third = 0;
        this.total = 0;
        this.position = 0;

    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getThird() {
        return third;
    }

    public void setThird(int third) {
        this.third = third;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
