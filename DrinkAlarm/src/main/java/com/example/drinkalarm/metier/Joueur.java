package com.example.drinkalarm.metier;

import com.example.drinkalarm.R;

/**
 * Classe métier Joueur
 */
public class Joueur {

    /**
     * Nom du joueur
     */

    private String nom;

    /**
     * Score
     */
    private Integer score = 0;

    /**
     * Actif
     */
    private Boolean actif = true;


    public Joueur(String nom) {
        this.nom = nom;
    }

    public Joueur(){}


    public Boolean isActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
