package com.example.drinkalarm.presentation;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drinkalarm.R;
import com.example.drinkalarm.application.JeuControlleur;
import com.example.drinkalarm.metier.Action;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.example.drinkalarm.R.layout.drawer_list_item;

public class JeuActivite extends ActionBarActivity {

    //CONSTANTES ACTIONS
    private final int AJOUT = 1;
    private final int MODIFICATION = 0;

    /**
     * Groupe de level
     */
    private RadioGroup level;

    /**
     * Gestion de la musique
     */
    private MediaPlayer mp;

    /**
     * Affichage du level
     */
    private ImageView level_image;

    /**
     * Layout - Liste des joueurs
     */
    private DrawerLayout mDrawerLayout;

    /**
     *
     */
    private ActionBarDrawerToggle mDrawerToggle;

    /**
     * Liste des joueurs
     */
    private ListView mDrawerList;

    /**
     * Liste des évènements relatifs à la partie : logs
     */
    //action courante
    private TextView titreAction;
    private TextView descAction;
    private ImageView imgAction;
    private LinearLayout layoutAction;
    //logs
    private ListView logsLv;
    //Création d'un SimpleAdapter qui se chargera de mettre les items présentes dans notre list (actionCourItem) dans la vue affichageitem
    SimpleAdapter adaptLogs;
    //Création de la ArrayList qui nous permettra de remplire la listView
    ArrayList<HashMap<String, String>> itemsLogs = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> allEvents = new ArrayList<HashMap<String, String>>();
    //On déclare la HashMap qui contiendra les informations pour un item
    HashMap<String, String> mapLogs;


    /**
     * Controller
     */
    private JeuControlleur controller;

    private CharSequence title;

    // Multi-Thread : gestion du chrono
    private Handler handler = new Handler();
    private TextView chrono;
    /**     * L'AtomicBoolean qui gère la destruction de la Thread de background     */
    AtomicBoolean isRunning = new AtomicBoolean(true);

    //MODE VIBRATION : LEGEND
    // This example will cause the phone to vibrate "SOS" in Morse Code
    // In Morse Code, "s" = "dot-dot-dot", "o" = "dash-dash-dash"
    // There are pauses to separate dots/dashes, letters, and words
    // The following numbers represent millisecond lengths
    int dot = 200;      // Length of a Morse Code "dot" in milliseconds
    int dash = 500;     // Length of a Morse Code "dash" in milliseconds
    int short_gap = 200;    // Length of Gap Between dots/dashes
    int medium_gap = 500;   // Length of Gap Between Letters
    int long_gap = 1000;    // Length of Gap Between Words
    private long[] pattern_vib = {
            0,  // Start immediately
            long_gap, short_gap, dot, dot, long_gap
    };

    //Préférences
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Set<String> listes;

    //CODE RETOUR
    public static final int CODE_RETOUR = 0;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //On arrête le chrono
        isRunning.set(false);
        handler.removeCallbacks(run);
        //On arrête le lecteur de musique
        if (mp.isPlaying())
            mp.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    /**
     * Called when the activity is first created.
     */
    @SuppressLint("ResourceAsColor")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Instanciation du lecteur audio
        mp = new MediaPlayer();
        //Mise en place du vibreur
        final Vibrator vib=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        // Date
        Date now;
        // Personnalisation d'une date
        DateFormat mediumDateFormat = DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM, DateFormat.MEDIUM);

        //Confirmation MODE LEGEND
        final boolean[] confirm = {false};

        /**
         * Initialisation
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //Ajouter la possibilité de modifier les préférences
        editor = preferences.edit();

        // Controller init
        controller = new JeuControlleur("medium");
        controller.addJoueur(preferences.getString(SettingsActivity.NOM_DEFAUT, getString(R.string.nom_defaut)));

        /**
         * Gestion level
         */
        level = (RadioGroup) findViewById(R.id.level);
        level_image = (ImageView) findViewById(R.id.level_image);
        level.check(getResources().getIdentifier(
                controller.getMode().getLibelle(), "id", getPackageName()));
        level.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                switch (checkedId) {
                    case R.id.soft:
                        confirm[0] = false;
                        controller.setMode("soft");
                        level_image.setImageResource(R.drawable.soft);
                        break;
                    case R.id.medium:
                        confirm[0] = false;
                        controller.setMode("medium");
                        level_image.setImageResource(R.drawable.medium);
                        break;
                    case R.id.hard:
                        confirm[0] = false;
                        controller.setMode("hard");
                        level_image.setImageResource(R.drawable.hard);
                        break;
                    case R.id.legend:
                        //On ne demande pas confirmation de passer en MODE LEGEND quand l'écran bascule
                            if (!confirm[0]) {
                                level.clearCheck();
                                /**
                                 * ALERT DIALOG :Confirmation Niveau LEGEND
                                 */
                                new AlertDialog.Builder(JeuActivite.this)
                                        .setTitle("Danger de mort")
                                        .setIcon(R.drawable.tete_de_mort)
                                        .setMessage("Etes-vous sûr de vouloir passer en mode LEGEND ?")
                                        .setPositiveButton("Oui", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                //Changement de mode
                                                confirm[0] = true;
                                                level_image.setImageResource(R.drawable.legend);
                                                level.check(R.id.legend);
                                                controller.setMode("legend");
                                                //Le téléphone vibre
                                                vib.vibrate(pattern_vib,- 1);
                                                //Message
                                                Toast.makeText(getApplicationContext(), "Bon courage...", Toast.LENGTH_LONG).show();
                                            }
                                        })
                                        .setNegativeButton("Non", new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                level.check(getResources().getIdentifier(
                                                        controller.getMode().getLibelle(), "id", getPackageName()));
                                                dialogInterface.cancel();
                                            }
                                        })
                                        .show();
                            }
                        break;
                }
            }
        });

        /**
         * Gestion liste des joueurs
         */
        title = getSupportActionBar().getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.listView);
        // Adapter pour la liste des joueurs
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, drawer_list_item, controller.getJoueursAsString()));
        /**
         * EVENEMENT -> Ajout d'un joueur
         */
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        /**
         * EVENEMENT -> Suppression d'un joueur
         */
        mDrawerList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Récupération Joueur sélectionné
                final int position = i;
                final String name = mDrawerList.getAdapter().getItem(position).toString();

                /**
                 * ALERT DIALOG : Confirmation Suppression
                 */
                new AlertDialog.Builder(JeuActivite.this)
                        .setTitle("Confirmation")
                        .setIcon(R.drawable.warning_icon)
                        .setMessage("'" + name + "' s'en va ?")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Distinction liste vide ou non
                                if (mDrawerList.getAdapter().getCount() != 1) {
                                    /*
                                    /Suppression  nom...
                                    */
                                    //... au niveau du controlleur
                                    controller.deleteJoueur(position);

                                    //... au niveau de l'affichage
                                    mDrawerList.setAdapter(new ArrayAdapter<String>(getApplicationContext(), drawer_list_item, controller.getJoueursAsString()));

                                    //On vérifie si le joueur par défaut a été effacé
                                    if (controller.getJoueurs().get(0).getNom().compareTo(preferences.getString(SettingsActivity.NOM_DEFAUT, getString(R.string.nom_defaut))) != 0) {
                                        //modfication : nouveau joueur par défaut
                                        editor.putString(SettingsActivity.NOM_DEFAUT, controller.getJoueurs().get(0).getNom());
                                        editor.commit();
                                    }
                                }
                            }
                        })
                        .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
                return true;
            }
        });

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open, /* "open drawer" description for accessibility */
                R.string.drawer_close /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                // supportInvalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                // supportInvalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Bouton AJOUTER JOUEUR
        final Button ajoutJoueur = (Button) findViewById(R.id.add_player);
        ajoutJoueur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Contrôle du nombre max de participants au jeu
                if (controller.getNbJoueurs() < controller.MAX_PARTICIPANTS)
                    saisieNom(1, mDrawerList.getCount());
                else {
                    //Message Liste pleine
                    new AlertDialog.Builder(JeuActivite.this)
                            .setTitle("Trop de soûlards !")
                            .setIcon(R.drawable.sys_error)
                            .setMessage("Nombre maximal de participants atteint pour la partie...")
                            .setNegativeButton("Retour", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            })
                            .show();
                }
            }
        });

        /**
         * Initialisation logs de jeu
         */
        titreAction =(TextView) findViewById(R.id.titre_action);
        descAction =(TextView) findViewById(R.id.description_action);
        imgAction = (ImageView) findViewById(R.id.img_action);
        layoutAction = (LinearLayout) findViewById(R.id.layout_action);

        logsLv = (ListView) findViewById(R.id.logsActionsLV);
        adaptLogs = new SimpleAdapter (this.getBaseContext(), itemsLogs, R.layout.item_event,
                new String[] {"titre", "description"}, new int[] {R.id.titre_log, R.id.description_log});

        /**
         * Gestion du chrono et démarrage du jeu
         */
        //Appel à un thread toutes les secondes
        chrono = (TextView) findViewById(R.id.compteur);
        handler.postDelayed(run, 500);
    }

    // Utilisation de l'interface Runnable pour le multi-threading
    Runnable run = new Runnable() {
        @Override
        public void run() {
            if (isRunning.get()) {
                updateTime();
            }
        }
    };

    //Méthode publique qui met à jour l'apparence du chrono
    public void changeColor() {
        if (getSecondes() == 0) {
            if (chrono.getText().toString().compareTo("00:00") == 0) {
                //couleur texte verte
                chrono.setTextColor(Color.rgb(0,100,0));
                chrono.setTypeface(null, Typeface.NORMAL);
            }
        }
        else if (getSecondes() <= 11) {
            //couleur texte rouge
            chrono.setTextColor(Color.rgb(255,0,0));
            chrono.setTypeface(null, Typeface.BOLD);
        }
        else {
            if (getSecondes() <= 31)
                //couleur texte orange
                chrono.setTextColor(Color.rgb(255,140,0));
        }
    }

    //Méthode publique qui gère l'utilisation du chrono
    public void updateTime() {
        changeColor();
        if (getSecondes() == 0) {
            //Cas lancement d'une ACTION !!
            if (chrono.getText().toString().compareTo("00:00") == 0) {
                //Génération des nombres aléatoires
                final Action action = this.controller.tour();

                if(action != null){
                    // Personnalisation d'une date
                    final DateFormat mediumDateFormat = DateFormat.getDateTimeInstance(
                            DateFormat.MEDIUM, DateFormat.MEDIUM);
                    //Date d'aujourd'hui
                    final Date now = new Date();
                    final String dteFormatee = mediumDateFormat.format(now);

                    //Apparition d'une Action
                    layoutAction.setBackgroundColor(Color.rgb(34,34,34));
                    final String affTitre = action.getTitreAction() + "  -  (" + dteFormatee + ")";
                    String affDesc = action.play(controller.getJoueurs());
                    titreAction.setText(affTitre);
                    descAction.setText(affDesc);
                    imgAction.setImageResource(R.drawable.marteau_tour_appel);
                    //On insère les éléments communs à une Action dans un objet HashMap
                    mapLogs = new HashMap<String, String>();
                    mapLogs.put("titre", affTitre);
                    mapLogs.put("description", affDesc);

                    //On ajoute notre action à la listeView
                    allEvents.add(0, mapLogs);
                    for (HashMap<String, String> unLog : allEvents) {
                        if (allEvents.indexOf(unLog) != 0)
                            if (itemsLogs.indexOf(unLog) == -1)
                                itemsLogs.add(0,unLog);
                    }
                    logsLv.setAdapter(adaptLogs);

                    //Chargement d'une chanson
                    mp = MediaPlayer.create(getApplicationContext(),JeuControlleur.getSon(action.getCheminSon()));
                    mp.start();
                    //Mise en place d'un écouteur sur l'image (pour faire appel)
                    imgAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Message de confirmation
                            new AlertDialog.Builder(JeuActivite.this)
                                    .setTitle("Décision du " + dteFormatee + " : " + action.getTitreAction())
                                    .setIcon(R.drawable.warning_icon)
                                    .setMessage("Souhaitez-vous vraiment faire appel ?")
                                    .setPositiveButton("Oui", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //Détermination de la décision d'appel
                                            Toast.makeText(getApplicationContext(), "En travaux...", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .setNegativeButton("Non", new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    })
                                    .show();
                        }
                    });
                }
            }
            chrono.setText("00:59");
        }
        else {
            if (getSecondes() <= 10)
                chrono.setText("00:0" + (getSecondes() - 1));
            else
                chrono.setText("00:" + (getSecondes() - 1));
        }
        handler.postDelayed(run, 500);
    }

    //Méthode qui détermine le nombre de secondes affichées sur le chronomètre
    public int getSecondes() {
        String secondes = chrono.getText().toString();
        //Cas chrono où il reste moins de 10s
        if (secondes.length() == 5)
            secondes = secondes.substring(secondes.length()-2, secondes.length());
        else
            secondes = secondes.substring(secondes.length() - 1, secondes.length());
        return Integer.parseInt(secondes);
    }

    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position,
                                long id) {
            selectItem(position);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        switch (item.getItemId()) {
            case R.id.game_settings:
                /**
                 * Charger la vue : Options de Jeu
                 */
                //On créé l'Intent qui va nous permettre d'afficher l'autre Activity
                Intent intent = new Intent(JeuActivite.this, SettingsActivity.class);

                //On démarre l'autre Activity
                //Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
                startActivityForResult(intent,CODE_RETOUR);
                break;
            case R.id.save_players:
                /**
                 * Enregistrer la liste actuelle de joueurs
                 */
                //Apparition boîte de dialogue pour nommer la liste de joueurs
                /*LayoutInflater inflater = this.getLayoutInflater();
                final View alertDialogView = inflater.inflate(R.layout.saisie_noms, null);

                //Création de l'AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                //On affecte la vue personnalisé que l'on a crée à notre AlertDialog
                builder.setView(alertDialogView);

                TextView nomListe = (TextView) alertDialogView.findViewById(R.id.tV_nom_joueur);
                final EditText repListe = (EditText) alertDialogView.findViewById(R.id.nom_joueur);
                repListe.setHint(R.string.hint_liste);
                nomListe.setText("Nom de la liste :");
                nomListe.setBackgroundColor(Color.rgb(0,100,0));

                builder.setNeutralButton(getString(R.string.valid), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Récupération du nom entré par l'utilisateur
                        String name = repListe.getText().toString();

                        //Contrôle champ 'Nom'
                        int retour = JeuControlleur.isValidListeName(name);
                        if (retour == JeuControlleur.OK) {
                                //On formate d'abord la chaine entree avant de l'utiliser
                                name = JeuControlleur.formatStr(name);
                                listes.add(name);
                                listes.addAll(controller.getJoueursAsString());
                                editor.putStringSet(SettingsActivity.LISTE, listes);
                                //Sauvegarde de la liste
                                Toast.makeText(getApplicationContext(), "Liste sauvée", Toast.LENGTH_SHORT).show();
                        } else {
                            erreurNom(retour, name);
                        }
                    }
                });
                builder.create();
                builder.show();*/
                Toast.makeText(getApplicationContext(), "En travaux...", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Méthode appelée à la sortie d'une autre activité
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Vérification du code de retour
        if(requestCode == CODE_RETOUR) {
            // On met à jour les nouvelles données
            majNom(preferences.getString(SettingsActivity.NOM_DEFAUT, getString(R.string.nom_defaut)), 0);

            // Vérifie que le résultat est OK
            if(resultCode == RESULT_OK) {
                //Toast.makeText(getApplicationContext(),"result_ok", Toast.LENGTH_SHORT).show();

                // Si l'activité est annulé
            } else if (resultCode == RESULT_CANCELED) {
                // On affiche que l'opération est annulée
                //Toast.makeText(this, "Opération annulée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(final int position) {
        saisieNom(0, position);
    }

    /**
     * APPUI SUR LA TOUCHE RETOUR
     */
    @Override
    public void onBackPressed() {
        //Message de confirmation avant de quitter le jeu
        new AlertDialog.Builder(JeuActivite.this)
                .setTitle("Confirmation fin de la partie")
                .setIcon(R.drawable.point_interrogation)
                .setMessage("Vous arrêtez déja l'apéro ?")
                .setPositiveButton("Oui, je n'en peux plus...", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //On quitte le jeu
                        JeuActivite.this.finish();
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();
    }

    //Méthode qui gère l'affichage d'une boîte de dialogue pour la saisie d'un nom : le paramètre
    //action sert à déterminer si on est en mode MODIFICATION ou AJOUT de joueur
    //le paramètre position représente l'entier correspondant à la position d'un item dans une
    //listView
    @SuppressLint("ResourceAsColor")
    public void saisieNom(final int action, final int position) {
        /*
        **  Saisie nom d'un joueur
         */
        //On instancie notre layout en tant que View
        LayoutInflater inflater = this.getLayoutInflater();
        final View alertDialogView = inflater.inflate(R.layout.saisie_noms, null);

        //Création de l'AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //On affecte la vue personnalisé que l'on a crée à notre AlertDialog
        builder.setView(alertDialogView);

        TextView nouvNom = (TextView) alertDialogView.findViewById(R.id.tV_nom_joueur);
        if (action == AJOUT) {
            nouvNom.setText("Nouveau Joueur :");
            nouvNom.setBackgroundColor(Color.rgb(0,100,0));
        }
        else if (action == MODIFICATION) {
            nouvNom.setText("Nouveau Nom :");
            nouvNom.setBackgroundColor(Color.rgb(0,6,43));
        }

        builder.setNeutralButton(getString(R.string.valid), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Récupération du nom entré par l'utilisateur
                final EditText eT_nom_joueur = (EditText) alertDialogView.findViewById(R.id.nom_joueur);
                String name = eT_nom_joueur.getText().toString();

                //Contrôle champ 'Nom'
                int retour = JeuControlleur.isValidPersonName(name);
                if (retour == JeuControlleur.OK) {
                    //On formate d'abord la chaine entree avant de l'utiliser
                    name = JeuControlleur.formatStr(name);

                    if (action == AJOUT) {
                        /**
                         * Ajout  nom...
                         */
                        //... au niveau du controlleur
                        name = JeuControlleur.doublon(controller.getJoueursAsString(), name);
                        controller.addJoueur(name);

                        //... au niveau de l'affichage
                        mDrawerList.setAdapter(new ArrayAdapter<String>(getApplicationContext(), drawer_list_item, controller.getJoueursAsString()));
                    }
                    else {
                        /**
                         * Modification  nom...
                         */
                        majNom(name, position);
                        if (position == 0) {
                            editor.putString(SettingsActivity.NOM_DEFAUT, name);
                            editor.commit();
                        }
                    }
                } else {
                    erreurNom(retour, name);
                }
            }
        });
        builder.create();
        builder.show();
    }

    //Méthode publique qui modifie le nom d'un joueur durant une partie
    public void majNom(String name, int position) {
        //... au niveau du controlleur
        if (position != 0)
            name = JeuControlleur.doublon(controller.getJoueursAsString(), name);
        controller.updateJoueur(name, position);

        //... au niveau de l'affichage
        mDrawerList.setAdapter(new ArrayAdapter<String>(getApplicationContext(), drawer_list_item, controller.getJoueursAsString()));
    }

    //Méthode publique qui affiche une boîte de dialogue d'erreur : SAISIE NOM incorrecte
    public void erreurNom(int codeRetour, String name) {
        AlertDialog.Builder builderErreur = new AlertDialog.Builder(JeuActivite.this);
        builderErreur = JeuControlleur.completeBuilderErreur(builderErreur, codeRetour, name);
        builderErreur.create();
        builderErreur.show();
    }
}
