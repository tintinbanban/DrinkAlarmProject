package com.example.drinkalarm.metier;

import java.util.ArrayList;

/**
 * Created by Tocard on 11/03/14.
 */
public class Liste {
    //Attributs de classe
    private String nomListe;
    private ArrayList<Joueur> joueurs;

    //Constructeur(s)
    public Liste() {
        super();
    }

    public Liste(ArrayList<Joueur> joueurs) {
        super();
        setJoueurs(joueurs);
    }

    public Liste(String nomListe, ArrayList<Joueur> joueurs) {
        super();
        setNomListe(nomListe);
        setJoueurs(joueurs);
    }

    //Getteurs & Setteurs
    public String getNomListe() {
        return this.nomListe;
    }

    public void setNomListe(String nomListe) {
        this.nomListe = nomListe;
    }

    public ArrayList<Joueur> getJoueurs() {
        return this.joueurs;
    }

    public void setJoueurs(ArrayList<Joueur> joueurs) {
        this.joueurs = joueurs;
    }

    //Méthodes supplémentaires

}
