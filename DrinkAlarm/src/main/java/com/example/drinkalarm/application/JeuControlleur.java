package com.example.drinkalarm.application;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.drinkalarm.R;
import com.example.drinkalarm.metier.*;

import org.xmlpull.v1.XmlPullParserException;

/**
 * Controlleur principale du jeu
 */
public class JeuControlleur {

    private ArrayList<Joueur> joueurs;
    private ArrayList<String> name_joueur;

    private Collection<Action> actions = new ArrayList<Action>();
    private Mode mode;
    private Collection<Integer> randomAlarmNumbers = new ArrayList<Integer>();

    // Différents mode de jeu
    private Mode soft = new Mode("soft",20F);
    private Mode medium = new Mode("medium",30F);
    private Mode hard = new Mode("hard",50F);
    private Mode legend = new Mode("legend",80F);

    //CARACTERES POUR LE FORMATAGE : LISTE DE JOUEURS
    public final static String NOM_LISTE_NEXT = "****";
    public final static String NOM_JOUEURS_NEXT = "////";
    public final static String FIN_LISTE_NEXT = "\\\\";

    // CONSTANTE NB MAX DE JOUEURS
    public final Integer MAX_PARTICIPANTS = 20;

    /**
     * Méthode permettant d'intialiser les paramètres du jeu :
     * - les différents modes
     * - les différentes actions
     * Insertion du mode de la partie
     * @param mode Mode de la partie
     */
    public JeuControlleur(String mode, String[] name_joueur, InputStream inAction) {
        joueurs = new ArrayList<Joueur>();
        this.name_joueur = new ArrayList<String>(Arrays.asList(name_joueur));
        ActionXmlParser parserAction = new ActionXmlParser();

        try {
            this.actions = parserAction.parse(inAction);
        } catch (XmlPullParserException e) {
            throw new RuntimeException();

        } catch (IOException e) {
            throw new RuntimeException();
        }

        setMode(mode);
    }

    public void setMode(String mode) {
        // Attribution du mode de jeu
        if (mode.equals("soft")) {
            this.mode = soft;

        } else if (mode.equals("medium")) {
            this.mode = medium;

        } else if (mode.equals("hard")) {
            this.mode = hard;

        } else if (mode.equals("legend")) {
            this.mode = legend;

        } else {
            this.mode = medium;
        }
    }

    /**
     * Teste de façon aléatoire si le joueur joue l'action ou pas
     * @param collection
     * @param proba
     * @param randomNumber
     * @return Boolean
     */
    private Boolean testDrink(Collection<Integer> collection, Float proba,
                              Integer randomNumber) {
        int nb = 0;

        collection.clear();
        // Génération aléatoire de n nombres
        int i = 0;
        while (i < proba) {
            nb = ((int) (Math.random() * 100.0));
            if (!collection.contains(nb)) {
                collection.add(nb);
                i++;
            }
        }

        // On regarde si le nombre généré est contenu dans la
        // collection
        return collection.contains(randomNumber);
    }

    /**
     * Nouveau tour de jeu. Renvoi l'action à faire
     * @return String
     */
    public Action tour() {
        Action action;
        Iterator<Action> iterator = this.actions.iterator();

        int randomNumber = (int) (Math.random() * 100.0);

        if(testDrink(randomAlarmNumbers, mode.getChance(), randomNumber)){
            //System.out.println("on peut boire !");
            // répartition des nombres aléatoires
            while (iterator.hasNext()) {
                action = iterator.next();

                if (testDrink(randomAlarmNumbers, action.getChance(mode.getLibelle()), randomNumber)) {
                    return action;
                }
            }
        }
        return null;
    }

    public ArrayList<Joueur> getJoueurs() {
        return joueurs;
    }


    public void addJoueur(String nom) {
        int id = randomIdJoueur();
        this.joueurs.add(new Joueur(JoueurFormat.formatStr(nom) + " " + name_joueur.get(id),id));
    }

    private int randomIdJoueur(){
        Integer id = (int) (Math.random() * name_joueur.size());
        id %= name_joueur.size();
        if(id <= 0) id=0;
        if(id >= name_joueur.size()) id--;
        for(Joueur j : joueurs){
            if(j.getId().equals(id))
                return randomIdJoueur();
        }
        return id;
    }

    public void updateJoueur(String nom, int position) {
        int id = randomIdJoueur();
        joueurs.set(position, new Joueur(JoueurFormat.formatStr(nom) + " " + name_joueur.get(id),id));
    }

    public void deleteJoueur(int position) {
        joueurs.remove(position);
    }

    public int getNbJoueurs() {
        return joueurs.size();
    }

    public Mode getMode() {
        return mode;
    }


}
