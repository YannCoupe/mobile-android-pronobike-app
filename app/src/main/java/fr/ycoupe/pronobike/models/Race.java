package fr.ycoupe.pronobike.models;

/**
 * Created by Yann on 22/02/16.
 */
public class Race {

    private int idRace;
    private String date;

    public Race(){

        this.idRace = 0;
        this.date = "";

    }

    public int getIdRace() {
        return idRace;
    }

    public void setIdRace(int idRace) {
        this.idRace = idRace;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
