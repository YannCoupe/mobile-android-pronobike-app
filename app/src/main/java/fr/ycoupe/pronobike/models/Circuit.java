package fr.ycoupe.pronobike.models;

/**
 * Created by Yann on 22/02/16.
 */
public class Circuit {

    private int idCircuit;
    private String name;
    private String localisation;
    private String countryCode;

    public Circuit(){
        init();
    }

    private void init(){

        this.idCircuit = 0;
        this.name = "";
        this.localisation = "";
        this.countryCode = "";

    }

    public int getIdCircuit() {
        return idCircuit;
    }

    public void setIdCircuit(int idCircuit) {
        this.idCircuit = idCircuit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
