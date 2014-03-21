/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package serveurweb;



import java.net.*;
import java.io.*;
/**
 *
 * @author Isabelle
 */
public class ServeurWeb {

    //attributs
    int port = 80; //valeur par defaut
    final int NUMPORTMAX = 65535;
    final int MAXCONNEXION = 3;
    public static int NbrConnexion = 0;
    int NumSession = 1;
    
    void SetPort(int p)
    {
        if (p > 0 && p < NUMPORTMAX)
        {
            this.port = p;
        }
    }
    void AfficherPort()
    {
        System.out.println("Serveur en ligne (port TCP " + port + ")");
    }
    //constructeur
    public ServeurWeb (String tab[])
    {
        if(tab.length == 1)
        {             
            try
            {
                int p = Integer.parseInt(tab[0]);
                SetPort(p);
            }
            catch (Exception e) { AfficherPort(); }
        }
    }
    
    public void Traitement()
    {
        try
        {
            ServerSocket serveur = new ServerSocket( port );
            AfficherPort();
            while(true)
            {
                if (NbrConnexion < MAXCONNEXION)
                {
                    Socket client = serveur.accept();
                    System.out.println( "Ouverture de la connexion " + NumSession );
                    //...creer session
                    Session session = new Session(client,NumSession);
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
            
            
        }
        catch ( IOException ioe )
        {
            System.out.println("Port non disponible le processus vas maintenant s'arrêter");
        }
    }
    
    public static void main( String args[] )
    {
        ServeurWeb serveur = new ServeurWeb(args);
        if ( serveur != null)
        {
            serveur.Traitement();
        }
    }
}
