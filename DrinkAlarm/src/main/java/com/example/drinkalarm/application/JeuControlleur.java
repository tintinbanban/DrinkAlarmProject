package com.example.drinkalarm.application;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.drinkalarm.R;
import com.example.drinkalarm.metier.*;

/**
 * Controlleur principale du jeu
 */
public class JeuControlleur {

    private ArrayList<Joueur> joueurs;
    private Collection<Action> actions = new ArrayList<Action>();
    private Mode mode;
    private Collection<Integer> randomAlarmNumbers = new ArrayList<Integer>();

    // Différents mode de jeu
    private Mode soft = new Mode("soft",20F);
    private Mode medium = new Mode("medium",30F);
    private Mode hard = new Mode("hard",50F);
    private Mode legend = new Mode("legend",80F);

    // CONSTANTES CHEMINS DES FICHIERS
    private final String HORNTUNE = "DrinkAlarm/res/horn_tune.wav";
    private final String DANSE_DU_VENTRE = "DrinkAlarm/res/danse_du_ventre.wav";
    private final String CALL_TO_ARMS = "DrinkAlarm/res/call_to_arms.wav";
    private final String TOUR_DE_FRANCE = "DrinkAlarm/res/tour_de_france.wav";
    private final String AIR_RAID_2 = "DrinkAlarm/res/air_raid_2.wav";
    private final String POUET_POUET = "DrinkAlarm/res/pouet_pouet.wav";
    private final String WOW = "DrinkAlarm/res/wow.wav";

    //CONSTANTES code retour(nom) -> 0 : OK / 1 : non valide / 3 : chaine vide /4 : chaine trop petite ou trop grande
    public final static int OK = 0;
    public final static int ERR = 1;
    public final static int EMPTY = 2;
    public final static int DEF_LENGTH = 3;

    //CONSTANTES MESSAGES
    public static final String ERR_NOM_MESS = "Le nom entré contient des caractères invalides : ";
    public static final String EMPTY_NOM_MESS = "La chaîne saisie est vide !";
    public static final String ERR_LENGTH_MESS = "Le nom doit etre compris entre 3 et 15 caractères : ";

    //CARACTERES POUR LE FORMATAGE : LISTE DE JOUEURS
    public final static String NOM_LISTE_NEXT = "****";
    public final static String NOM_JOUEURS_NEXT = "////";
    public final static String FIN_LISTE_NEXT = "\\\\";

    // Tableau d'association des chemins de chansons String - Integer
    private static Hashtable<String, Integer> collecChansons;

    // Liste de noms de joueurs : suggestions
    private static Hashtable<Integer,String> collecNoms;

    // CONSTANTE NB MAX DE JOUEURS
    public final Integer MAX_PARTICIPANTS = 20;

    /**
     * Méthode permettant d'intialiser les paramètres du jeu :
     * - les différents modes
     * - les différentes actions
     * Insertion du mode de la partie
     * @param mode Mode de la partie
     */
    public JeuControlleur(String mode) {
        joueurs = new ArrayList<Joueur>();
        collecNoms = new Hashtable<Integer, String>(50);
        collecChansons = new Hashtable<String, Integer>();

        // Remplissage du tableau pour la suggestion des noms
        collecNoms.put(1, "Alfred");
        collecNoms.put(2, "Georges");
        collecNoms.put(3, "Joris");
        collecNoms.put(4, "Jean-Albert");
        collecNoms.put(5, "Maurice");
        collecNoms.put(6, "Mike");
        collecNoms.put(7, "Batman");
        collecNoms.put(8, "Alphonse");
        collecNoms.put(9, "Jean");
        collecNoms.put(10, "Christophe");
        collecNoms.put(11, "Albert");
        collecNoms.put(12, "Thierry");
        collecNoms.put(13, "Eric");
        collecNoms.put(14, "Joël");
        collecNoms.put(15, "Wilfried");
        collecNoms.put(16, "Léon");
        collecNoms.put(17, "Titeuf");
        collecNoms.put(18, "Casper");
        collecNoms.put(19, "Martine");
        collecNoms.put(20, "Jean-Louis");
        collecNoms.put(21, "Jean-Paul");
        collecNoms.put(22, "Raymond");
        collecNoms.put(23, "Frédéric");
        collecNoms.put(24, "Dora");
        collecNoms.put(25, "Arnold");
        collecNoms.put(26, "Michel");
        collecNoms.put(27, "Jean-Michel");
        collecNoms.put(28, "Christian");
        collecNoms.put(29, "Louis");
        collecNoms.put(30, "José");
        collecNoms.put(31, "Maria");
        collecNoms.put(32, "Louise");
        collecNoms.put(33, "Marie-Louise");
        collecNoms.put(34, "Jean-Michel");
        collecNoms.put(35, "Hugues");
        collecNoms.put(36, "Roger");
        collecNoms.put(37, "Luc");
        collecNoms.put(38, "Serge");
        collecNoms.put(39, "Maryline");
        collecNoms.put(40, "Yvette");
        collecNoms.put(41, "Berthe");
        collecNoms.put(42, "Béatrice");
        collecNoms.put(43, "Joséphine");
        collecNoms.put(44, "Angela");
        collecNoms.put(45, "Ségolène");
        collecNoms.put(46, "Yves");
        collecNoms.put(47, "Marcel");
        collecNoms.put(48, "Superman");
        collecNoms.put(49, "Bioman");
        collecNoms.put(50, "Capitaine-Flamme");

        // Remplissage du tableau pour les correspondances des chemins des chansons
        collecChansons.put(HORNTUNE,R.raw.horn_tune);
        collecChansons.put(DANSE_DU_VENTRE,R.raw.danse_du_ventre);
        collecChansons.put(CALL_TO_ARMS,R.raw.call_to_arms);
        collecChansons.put(TOUR_DE_FRANCE,R.raw.tour_de_france);
        collecChansons.put(AIR_RAID_2,R.raw.air_raid_2);
        collecChansons.put(POUET_POUET,R.raw.pouet_pouet);
        collecChansons.put(WOW,R.raw.wow);

        // Attribution des actions
        Action a = new Action("A LA VOTRE", "... Olé !!!! Une gorgée pour TOUT le monde", HORNTUNE);
        a.setChance(soft, 40F);
        a.setChance(medium, 37F);
        a.setChance(hard, 30F);
        a.setChance(legend,50F);
        this.actions.add(a);

        a = new Action("BIZUT TIME", "Une gorgée pour le BIZUT", DANSE_DU_VENTRE);
        a.setChance(soft, 20F);
        a.setChance(medium,23F);
        a.setChance(hard, 20F);
        a.setChance(legend, 0F);
        a.setNbParticipant(1);
        this.actions.add(a);

        a = new Action("CUL SEC !", "Attention au bufffalo...", AIR_RAID_2);
        a.setChance(soft, 20F);
        a.setChance(medium,22F);
        a.setChance(hard,5F);
        a.setChance(legend, 13F);
        this.actions.add(a);

        a = new Action("FREEZE", "Ne bougez plus !", POUET_POUET);
        a.setChance(soft,10F);
        a.setChance(medium,8F);
        a.setChance(hard, 14F);
        a.setChance(legend, 13F);
        this.actions.add(a);

        a = new Action("KING TIME", "Tout le monde boit, sauf le ROI", CALL_TO_ARMS);
        a.setChance(soft,5F);
        a.setChance(medium,8F);
        a.setChance(hard, 30F);
        a.setChance(legend, 35F);
        a.setNbParticipant(1);
        this.actions.add(a);

        a = new Action("BOUFFON DU ROI ?"," gorgée(s) pour le bouffon du ROI", TOUR_DE_FRANCE);
        a.setChance(soft, 5F);
        a.setChance(medium, 2F);
        a.setChance(hard,1F);
        a.setChance(legend, 2F);
        a.setGorgeRamdom(true);
        a.setNbParticipant(1);
        this.actions.add(a);

        setMode(mode);
    }

    public JeuControlleur(){
        new JeuControlleur("medium");
    }

    //Getteur & Setteur
    public Hashtable<Integer, String> getCollecNoms() {
        return this.collecNoms;
    }

    public void addJoueur(String nom) {
        this.joueurs.add(new Joueur(nom));
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
            nb = ((int) (Math.random() * 10000000000000.0 / 100000000000.0));
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

        int randomNumber = (int) (Math.random() * 100000000000.0 / 1000000000.0);

        if(testDrink(randomAlarmNumbers, mode.getChance(), randomNumber)){
            //System.out.println("on peut boire !");
            // répartition des nombres aléatoires
            while (iterator.hasNext()) {
                action = iterator.next();

                if (testDrink(randomAlarmNumbers, action.getChance(mode), randomNumber)) {
                    return action;
                }
            }
        }
        return null;
    }

    public ArrayList<Joueur> getJoueurs() {
        return joueurs;
    }

    public ArrayList<String> getJoueursAsString(){
        ArrayList<String> retour = new ArrayList<String>();
        for(Joueur j : joueurs){
            retour.add(j.getNom());
        }
        return retour;
    }

    public void updateJoueur(String nom, int position) {
        joueurs.set(position, new Joueur(nom));
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

    //Méthode qui vérifie la validité d'un nom de liste saisi
    //mêmes codes retour 'isValidPersonName(name)'
    public static int isValidListeName(String name) {
        name = name.replace(" ", "");
        return isValidPersonName(name);
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

    //Méthode qui détermine la décision d'appel à renvoyer à l'utilisateur
    public Action obtenirDecision(Action action) {
        Action actionAppel = new Action("","",null);
        return  actionAppel;
    }

    //Méthode qui associe un message d'erreur en fonction du code retour de la saisie d'un nom
    public static String getMessErreur(int erreur) {
        switch (erreur) {
            case EMPTY :
                return EMPTY_NOM_MESS;
            case ERR :
                return ERR_NOM_MESS;
            case DEF_LENGTH :
                return ERR_LENGTH_MESS;
            default:
                return "Bug-Error...";
        }
    }

    //Méthode qui modifie et retourne une chaine de caractères afin d'effectuer des tests ou des modifications sur cette dernière
    public static String getSimpleName(String simpleName) {
        simpleName = simpleName.trim();
        simpleName = simpleName.toLowerCase();
        return simpleName;
    }

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

    //Méthode statique qui retourne le chemin d'un fichier musical sous forme d'un entier en fonction du chemin en
    // chaîne de caractères
    public static int getSon(String cheminSon) {
        return collecChansons.get(cheminSon);
    }

    //Méthode statique qui détecte et corrige les doublons avant l'insertion dans une liste
    public static String doublon(ArrayList<String> liste, String nom) {
        //Copie du nom en paramètre : utile en cas de récursivité
        String tempNom = nom;
        int random;

        // i = élément comparant
        int i = 0;
        while (i < liste.size()) {
            if (liste.get(i).compareTo(nom) == 0) {
                //Doublon détecté
                //Attribution d'un nom supplémentaire
                random = (int) (Math.random() * 500000000000.0 / 10000000000.0);
                //Correction
                nom = tempNom + "_" + collecNoms.get(random);
                doublon(liste, nom);
            }
            i++;
        }
        return nom;
    }

    //Méthode publique qui affiche une boîte de dialogue d'erreur : SAISIE NOM incorrecte
    public static AlertDialog.Builder completeBuilderErreur(AlertDialog.Builder build, int codeRetour, String name) {
        build.setTitle("ERREUR saisie !");

        build.setMessage(getMessErreur(codeRetour) + name);
        build.setIcon(R.drawable.sys_error);
        build.setNegativeButton("Retour", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                dialog.dismiss();}
        });
        return build;
    }

    public static void main(String args[]) {

        /*
        TEST DE LA FONCTION DE VERIFICATION DE NOM D'UNE PERSONNE
         */
        /*System.out.println("Jean -> " + JeuControlleur.isValidPersonName("Jean"));
        System.out.println("jean -> " + JeuControlleur.isValidPersonName("jean"));
        System.out.println("Jea -> " + JeuControlleur.isValidPersonName("Jea"));
        System.out.println("Je -> " + JeuControlleur.isValidPersonName("Je"));
        System.out.println("jan -> " + JeuControlleur.isValidPersonName("jan"));
        System.out.println("René -> " + JeuControlleur.isValidPersonName("René"));
        System.out.println("Jean-Paul -> " + JeuControlleur.isValidPersonName("Jean-Paul"));
        System.out.println("_Maurice -> " + JeuControlleur.isValidPersonName("_Maurice"));
        System.out.println("_maurice -> " + JeuControlleur.isValidPersonName("_maurice"));
        System.out.println("(Delf -> " + JeuControlleur.isValidPersonName("(Delf"));
        System.out.println(" Alphonse -> " + JeuControlleur.isValidPersonName(" Alphonse"));
        System.out.println("ma thias -> " + JeuControlleur.isValidPersonName("ma thias"));
        System.out.println("Jean-Baptiste_Alphonse -> " + JeuControlleur.isValidPersonName("Jean-Baptiste_Alphonse"));
        System.out.println("MArcel -> " + JeuControlleur.isValidPersonName("MArcel"));
        System.out.println("MARTY -> " + JeuControlleur.isValidPersonName("MARTY"));
        System.out.println("-_Arthur -> " + JeuControlleur.isValidPersonName("-_Arthur"));
        System.out.println("-Jean- -> " + JeuControlleur.isValidPersonName("-Jean-"));*/



        /*
        TEST DE LA FONCTION DE DETECTION ET CORRECTION DE DOUBLONS
         */
        /*String nom = "Jean";
        ArrayList<String> uneListe = new ArrayList<String>();
        uneListe.add("Maurice");
        uneListe.add("Jean");
        uneListe.add("Chantal");
        uneListe.add("Jean_02");
        uneListe.add("Jean_03");
        uneListe.add("Bernie");

        nom = doublon(uneListe, nom, 1);
        System.out.println("Résultat nom FINAL  -->  " + nom);
        System.out.println("\n\nContenu de la liste :");
        for (String unNom : uneListe) {
            System.out.println("- " + unNom);
        }*/

    }
}
