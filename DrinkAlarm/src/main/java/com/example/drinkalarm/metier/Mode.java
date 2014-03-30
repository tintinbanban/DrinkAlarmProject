package com.example.drinkalarm.metier;

/**
 * Classe métier Mode
 */
public class Mode {

    /**
     * Nom du mode de jeu
     */
    private String libelle;

    /**
     * Chance de boire pour un tour de jeu
     */
    private Float chance;

    public Mode(String libelle, Float chance) {
        this.libelle = libelle;
        this.chance = chance;
    }

    public Float getChance() {
        return chance;
    }

    public void setChance(Float chance) {
        this.chance = chance;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
}
