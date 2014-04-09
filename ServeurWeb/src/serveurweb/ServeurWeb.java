// ServeurWeb.java
// Fait par : Simon Boucahard et Isabelle Angrignon
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
    final int MAXCONNEXION = 666;
    final int DELAI = 500;
    public static int NbrConnexion = 0;
    int NumSession = 1;
    Thread threadTerminateur;
    
    
    void SetPort(int p)
    {
        if (p < NUMPORTMAX && p > 0)
        {
            this.port = p;
        }
    }
    void AfficherPort()
    {
        System.out.println("Serveur en ligne (port TCP " + port + ",racine="+pathRep+")");
    }
    //constructeur
    public ServeurWeb (String tab[]) throws Exception
    {
        if(tab.length == 1 || tab.length == 2 )
        {
            try
            {
                int p = Integer.parseInt(tab[0]);
                SetPort(p);
            }
            catch (Exception e) { /*Fait rien, utilise le port par défaut*/ }
        }
        if ( tab.length == 2)
        {
            try
            {
                File dossier = new File(tab[1]);
                if ( dossier.isDirectory())
                {
                    pathRep = tab[1];
                }
            }
            catch ( Exception e) {}
        }
        else if (!(new File(pathRep).isDirectory()))
        {
            throw new Exception("Le serveur a tenter de se lancer sur le répertoire par défaut "+ pathRep+" mais le répertoire n'existe pas !" );
        }
        Terminateur leTerminator = new Terminateur();
	threadTerminateur = new Thread(leTerminator);
	threadTerminateur.start();// au constructqeur, un thread lit en boucle        
    }
    
    public void Traitement() throws Exception
    {
       
        try
        {
            ServerSocket serveur = new ServerSocket( port );
            AfficherPort();
            while(threadTerminateur.isAlive())
            {
                try
                {
                    serveur.setSoTimeout(DELAI);
                    if (NbrConnexion < MAXCONNEXION)
                    {
                        Socket client = serveur.accept();
                        System.out.println( "Ouverture de la session " + NumSession );
                        //...creer session
                        Session session = new Session(client,NumSession,pathRep);
                        Thread t = new Thread(session);
                        t.start();
                        NbrConnexion++;
                        NumSession++;
                    }
                    else
                    {
                        try
                        {
                            Thread.sleep(1000);
                        }
                        catch (Exception e) { System.err.println( e ); }
                    }
                }
                catch( SocketTimeoutException ste )
                {
                   // le délai d'inactivité est expiré, on continue                   
                }
            }
            System.exit(0);
        }
        catch ( IOException ioe )
        {
            throw new Exception("Le serveur a tenter de se lancer sur ce port : "+ port+" mais il est déjà occuper");
        }
    } 
    
    public static void main( String args[] )
    {
        try
        {
            ServeurWeb serveur = new ServeurWeb(args);
            serveur.Traitement();
        }
        catch (Exception e) {System.out.println(e.getMessage());}
        
    }
}
