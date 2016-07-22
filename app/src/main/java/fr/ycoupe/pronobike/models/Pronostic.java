package fr.ycoupe.pronobike.models;

/**
 * Created by Yann on 22/02/16.
 */
public class Pronostic {

    private int idPronostic;
    private int gameId;
    private int raceId;
    private int first;
    private int second;
    private int third;

    public Pronostic(){

        this.idPronostic = 0;
        this.gameId = 0;
        this.raceId = 0;
        this.first = 0;
        this.second = 0;
        this.third = 0;

    }

    public int getIdPronostic() {
        return idPronostic;
    }

    public void setIdPronostic(int idPronostic) {
        this.idPronostic = idPronostic;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getRaceId() {
        return raceId;
    }

    public void setRaceId(int raceId) {
        this.raceId = raceId;
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
}
