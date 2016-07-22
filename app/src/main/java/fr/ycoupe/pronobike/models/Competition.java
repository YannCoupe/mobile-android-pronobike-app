package fr.ycoupe.pronobike.models;

/**
 * Created by Yann on 22/02/16.
 */
public class Competition {

    private int idCompetition;
    private String name;
    private String desc;
    private int current;

    public Competition(){
        init();
    }

    private void init(){

        this.idCompetition = 0;
        this.name = "";
        this.desc = "";
        this.current = 0;

    }

    public int getIdCompetition() {
        return idCompetition;
    }

    public void setIdCompetition(int idCompetition) {
        this.idCompetition = idCompetition;
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

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }
}
