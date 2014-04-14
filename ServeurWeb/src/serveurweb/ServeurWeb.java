// ServeurWeb.java
// Fait par : Simon Bouchard et Isabelle Angrignon
// Version 1: le 2014-03-31
// Gestion d'un serveur qui sera éventuellement web

package serveurweb;

import java.net.*;
import java.io.*;

public class ServeurWeb {
    
    //attributs
    int port = 80; //valeur par defaut
    String pathRep = "C:\\www"; // valeur par défaut
    final int NUMPORTMAX = 65535;
    final int MAXCONNEXION = 666; // Pour Daren
    final int DELAI = 500; // pour éviter de tourner dans le beure si on est a 667 conn et plus
    public static int NbrConnexion = 0; // Variable entre session et serveur pour communiquer le nombre de connexion
    int NumSession = 1;         // Le numero de session a titre informatif !!! 
    Thread threadTerminateur;   
    
    // S'assure que le port rentre dans les limitations des ports habituel.
    void SetPort(int p)
    {
        if (p < NUMPORTMAX && p > 0)
        {
            this.port = p;
        }
    }
    // Entete du serveur
    void AfficherPort()
    {
        System.out.println("Serveur en ligne (port TCP " + port + ",racine="+pathRep+")");
    }
    
    
    //constructeur
    public ServeurWeb (String tab[]) throws Exception
    {
        GestionParametre(tab);
        if (!(new File(pathRep).isDirectory())) // Si le dossier par défaut n'existe pas et que le dossier n'a pas été enter ou est incorecte on court apres le trouble
        {
            throw new Exception("Le serveur a tente de se lancer sur le repertoire par defaut "+ pathRep+" mais ce repertoire n'existe pas !" );
        }
        Terminateur leTerminator = new Terminateur();   // Initialisation du terminateur qui tuera le serveur si on entre la touche Q
	threadTerminateur = new Thread(leTerminator);
	threadTerminateur.start();// au constructqeur, un thread lit en boucle        
    }
    
    
    void GestionParametre (String tab[])
    {
        if(tab.length == 1 || tab.length == 2 )            // On veut être sur qu'il n'y ai que un ou deux parametre
        {
            try
            {
                int p = Integer.parseInt(tab[0]);           // Pour s'assurer que on a pas entrer genre allo sur la ligne de commande
                SetPort(p);
            }
            catch (Exception e) { /*Fait rien, utilise le port par défaut*/ }
        }
        if ( tab.length == 2)               // Le second parametre est obligatoirement le parametre du path 
        {
            try
            {
                File dossier = new File(tab[1]);
                if ( dossier.isDirectory())         // Si le dossier passer en parametre existe ...
                {
                    pathRep = tab[1];               // Il devient le path sinon on garde celui par défaut www
                }
            }
            catch ( Exception e) {}
        }
        
    }
    
    // Gere le terminateur
    public void Traitement() throws Exception
    {
       
        try
        {
            ServerSocket serveur = new ServerSocket( port );    // |
            AfficherPort();                                     // | Bienvenue du serveur ! 
            while(threadTerminateur.isAlive())  // Tant que le terminateur n'est pas mort 
            {
                try
                {
                    serveur.setSoTimeout(DELAI);            // on fixe un delai d'attente de connexion
                    if (NbrConnexion < MAXCONNEXION)        
                    {
                        Socket client = serveur.accept();       // on attend la connexion le temps du DELAI
                        System.out.println( "Ouverture de la session " + NumSession );  
                        //...creer session
                        Session session = new Session(client,NumSession,pathRep);   // On crée la session qui servira de "serveur" au client
                        Thread t = new Thread(session);             // on le lance
                        t.start();
                        NbrConnexion++;     // le nbr de connexion sera décrémenter par la session
                        NumSession++;       // Le num de session ne décrémente jamais
                    }
                    else
                    {
                        try
                        {
                            Thread.sleep(DELAI);        // Si on est audessu du nombre de connexiom cette ligne évite que l'on tourne dans le beure
                        }
                        catch (Exception e) { System.err.println( e ); }
                    }
                }
                catch( SocketTimeoutException ste )
                {
                   // le délai d'inactivité est expiré, on continue                   
                }
            }
            System.exit(0);     // On termine le programme si le terminateur est mort
        }
        catch ( IOException ioe )
        {
            throw new Exception("Le serveur a tente de se lancer sur ce port : "+ port+" mais il est deja occupe");
        }
    }
    
    public static void main( String args[] )
    {
        try
        {
            ServeurWeb serveur = new ServeurWeb(args);
            serveur.Traitement();
        }
        catch (Exception e) {System.out.println(e.getMessage());}   // On peut se permettre de ne pas réinterpéter les messages d'érreure puisque ce sont les notres.
        
    }
}
