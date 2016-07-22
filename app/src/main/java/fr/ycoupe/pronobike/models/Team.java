package fr.ycoupe.pronobike.models;

/**
 * Created by Yann on 22/02/16.
 */
public class Team {

    private int idTeam;
    private String name;
    private String desc;

    public Team(){

        this.idTeam = 0;
        this.name = "";
        this.desc = "";

    }

    public int getIdTeam() {
        return idTeam;
    }

    public void setIdTeam(int idTeam) {
        this.idTeam = idTeam;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
