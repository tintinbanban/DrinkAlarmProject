package com.example.drinkalarm.metier;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe métier Action
 */
public class Action {
    /**
     * Titre significatif d'une action
     */
    private String titreAction;

    /**
     * Description de l'action
     */
    private String descAction;


    /**
     * Chemin du son é jouer
     */
    private String cheminSon;

    /**
     * Tableau de proba de l'action en fonction des modes de jeu
     * @see Mode
     */
    private Map<Mode,Float> chances = new HashMap<Mode,Float>();

    /**
     * Nombre de participant pour l'action.
     * 0 signifie que tout le monde participe
     */
    private Integer nbParticipant = 0;

    /**
     * Nombre de gorgé aléatoire
     */
    private Boolean gorgeRamdom = false;

    /** Constructeur
     *
     * @param titreAction
     * @param cheminSon
     */
    public Action(String titreAction, String descAction, String cheminSon) {
        this.titreAction = titreAction;
        this.descAction = descAction;
        this.cheminSon = cheminSon;
    }

    public Boolean getGorgeRamdom() {
        return gorgeRamdom;
    }

    public void setGorgeRamdom(Boolean gorgeRamdom) {
        this.gorgeRamdom = gorgeRamdom;
    }

    public Integer getNbParticipant() {
        return nbParticipant;
    }

    public void setNbParticipant(Integer nbParticipant) {
        this.nbParticipant = nbParticipant;
    }

    public Float getChance(Mode mode) {
        return (chances.containsKey(mode))?chances.get(mode):0F;
    }

    public void setChance(Mode mode, Float chance) {
        this.chances.put(mode,chance);
    }

    public String getCheminSon() {
        return cheminSon;
    }

    public void setCheminSon(String cheminSon) {
        this.cheminSon = cheminSon;
    }

    public String getTitreAction() {
        return titreAction;
    }

    public void setTitreAction(String titreAction) {
        this.titreAction = titreAction;
    }

    public String getDescAction() {
        return descAction;
    }

    public void setDescAction(String descAction) {
        this.descAction = descAction;
    }


    /**
     * Méthode qui choisi aléatoirement le joueur et le nombre de gorgé suivant les actions.
     *
     * @param joueurs Liste des joueurs
     * @return String
     */
    public String play(ArrayList<Joueur> joueurs) {
        String retour = "";

        //nbParticipants = nb de joueurs qui vont être concerné par l'évènement : cul sec ou une gorgée...
        if(nbParticipant != 0){
            if(joueurs.size() >= nbParticipant){
                int randomParticipants = (int) (Math.random() * ((joueurs.size() - 1) * 10000000 / 10000000));
                // System.out.println("participant : " + randomParticipants);
                retour = " : " + joueurs.get(randomParticipants).getNom();
            }
        }
        if(gorgeRamdom){
            int randomNbGorgees = (int) (Math.random() * (6 * 10000000 / 10000000));
            return randomNbGorgees + descAction + retour;
        }
        else
            return descAction + retour;
    }
}
