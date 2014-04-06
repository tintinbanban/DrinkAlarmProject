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

    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Joueur(String nom, Integer id) {
        this.nom = nom;
        this.id = id;
    }

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
