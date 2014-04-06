package com.example.drinkalarm.metier;

import com.example.drinkalarm.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by david on 05/04/14.
 */
public class JoueurFormat {
    //CONSTANTES code retour(nom) -> 0 : OK / 1 : non valide / 3 : chaine vide /4 : chaine trop petite ou trop grande
    public final static int OK = 0;
    public final static int ERR = 1;
    public final static int EMPTY = 2;
    public final static int DEF_LENGTH = 3;

    //Méthode qui formate les chaines de caractères d'une certaine façon : style nom de liste ou nom de joueur
    public static String formatStr(String name) {
        char[] tabName;
        int i;
        name = getSimpleName(name);

        //Capitalisation de la première lettre du nom
        String captital = name.substring(0,1);
        String otherLetters = name.substring(1);
        name = captital.toUpperCase() + otherLetters;

        tabName = name.toCharArray();
        for (i = 0; i < tabName.length; i++) {
            if (tabName[i] == ' ' || tabName[i] == '-' || tabName[i] == '_')
                //Capitalisation des lettres après espaces ' ' ou '-'
                tabName[i+1] = (char) (tabName[i+1] - 32);
        }
        name = new String(tabName);
        return name;
    }


    //Méthode qui modifie et retourne une chaine de caractères afin d'effectuer des tests ou des modifications sur cette dernière
    private static String getSimpleName(String simpleName) {
        simpleName = simpleName.trim();
        simpleName = simpleName.toLowerCase();
        return simpleName;
    }

    //Méthode qui vérifie la validité d'un nom de personne saisi
    public static int isValidPersonName(String name) {
        name = getSimpleName(name);
        //Vérification du contenu de la chaîne
        if (name.length() == 0)
            return EMPTY;
        if (name.length() < 3 || name.length() > 15)
            return DEF_LENGTH;
        // Expression qui sera recherchée dans le texte
        String regex = "^[a-zA-Z_éëçèôîùâ-]{3,15}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);

        if (matcher.matches())
            return OK;
        else
            return ERR;
    }


    //Méthode qui vérifie la validité d'un nom de liste saisi
    //mêmes codes retour 'isValidPersonName(name)'
    public static int isValidListeName(String name) {
        name = name.replace(" ", "");
        return isValidPersonName(name);
    }


    //Méthode qui associe un message d'erreur en fonction du code retour de la saisie d'un nom
    public static int getMessErreur(int erreur) {
        switch (erreur) {
            case JoueurFormat.EMPTY :
                return R.string.empty_nom_mess;
            case JoueurFormat.ERR :
                return R.string.err_nom_mess;
            case JoueurFormat.DEF_LENGTH :
                return R.string.err_length_mess;
            default:
                return R.string.err_default;
        }
    }
}
