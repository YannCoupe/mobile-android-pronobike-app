package fr.ycoupe.pronobike.models;

/**
 * Created by Yann on 22/02/16.
 */
public class Game {
    
    private int idGame;
    private String name;
    private String token;
    private int admin;
    private int idRace;
    private String dateRace;
    private String circuitRace;
    private String competitionRace;
    private int positionUser;

    public Game(){

        this.idGame = 0;
        this.name = "";
        this.token = "";
        this.admin = 0;
        this.idRace = 0;
        this.dateRace = "";
        this.circuitRace = "";
        this.competitionRace = "";
        this.positionUser = 0;

    }

    public int getIdGame() {
        return idGame;
    }

    public void setIdGame(int idGame) {
        this.idGame = idGame;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }

    public int getIdRace() {
        return idRace;
    }

    public void setIdRace(int idRace) {
        this.idRace = idRace;
    }

    public String getDateRace() {
        return dateRace;
    }

    public void setDateRace(String dateRace) {
        this.dateRace = dateRace;
    }

    public String getCircuitRace() {
        return circuitRace;
    }

    public void setCircuitRace(String circuitRace) {
        this.circuitRace = circuitRace;
    }

    public String getCompetitionRace() {
        return competitionRace;
    }

    public void setCompetitionRace(String competitionRace) {
        this.competitionRace = competitionRace;
    }

    public int getPositionUser() {
        return positionUser;
    }

    public void setPositionUser(int positionUser) {
        this.positionUser = positionUser;
    }
}
