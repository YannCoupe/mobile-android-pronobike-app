package fr.ycoupe.pronobike.models;

/**
 * Created by Yann on 22/02/16.
 */
public class User {

    private int idUser;
    private String firstname;
    private String lastname;
    private String email;

    public User(){

        this.idUser = 0;
        this.firstname = "";
        this.lastname = "";
        this.email = "";

    }

    public User(int idUser, String firstname, String lastname, String email){

        this.idUser = idUser;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;

    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
