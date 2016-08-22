package fr.ycoupe.pronobike.models;

import android.os.Parcel;
import android.os.Parcelable;

import fr.ycoupe.pronobike.utils.Logger;

/**
 * Created by Yann on 22/02/16.
 */
public class Game implements Parcelable {
    private final static String TAG = Game.class.getSimpleName();
    
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

    // =============================================================================================
    // Parcelable Interface

    public final static Creator<Game> CREATOR = new Creator<Game>() {
        public Game createFromParcel(final Parcel in) {
            return new Game(in);
        }

        public Game[] newArray(final int size) {
            return new Game[size];
        }
    };

    private Game(final Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        Logger.log(Logger.Level.VERBOSE, TAG, "writeToParcel");
        dest.writeInt(idGame);
        dest.writeString(name);
        dest.writeString(token);
        dest.writeInt(admin);
        dest.writeInt(idRace);
        dest.writeString(dateRace);
        dest.writeString(circuitRace);
        dest.writeString(competitionRace);
        dest.writeInt(positionUser);
    }

    private void readFromParcel(final Parcel in) {
        Logger.log(Logger.Level.VERBOSE, TAG, "readFromParcel");

        idGame = in.readInt();
        name = in.readString();
        token = in.readString();
        admin = in.readInt();
        idRace = in.readInt();
        dateRace = in.readString();
        circuitRace = in.readString();
        competitionRace = in.readString();
        positionUser = in.readInt();
    }
}
