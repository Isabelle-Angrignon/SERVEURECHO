// Serveur echo 
// Fait par : Simon Bouchard et Isabelle Angrignon
// Fait le  : 2014-03-17
// Gestion d'un serveur écho ( Gestion des attributuions des sessions )

package serveurecho;



import java.net.*;
import java.io.*;

class ServeurEcho
{
    //attributs
    int port = 7; //valeur par defaut
    final int NUMPORTMAX = 65535;
    final int MAXCONNEXION = 3;
    public static int NbrConnexion = 0;
    Session[] tableau = new Session[MAXCONNEXION];
    
    void SetPort(int p)
    {
        if (p > 0 && p < NUMPORTMAX)
        {
            this.port = p;
        }
    }
    //constructeur
    public ServeurEcho (String tab[])
    {
        if(tab.length == 1)
        {             
            try
            {
                int p = Integer.parseInt(tab[0]);
                SetPort(p);
            }
            catch (Exception e) { System.err.println( e ); }
        }        
    }
    
    public void Traitement()
    {
        try
        {
            ServerSocket serveur = new ServerSocket( port );
            System.out.println( "Serveur echo en ligne" );
            while(true)
            {
                if (NbrConnexion < MAXCONNEXION)
                {
                    Socket client = serveur.accept();
                    System.out.println( "Ouverture de la connexion " + NbrConnexion );
                    //...creer session
                    Session session = new Session(client,NbrConnexion);
                    Thread t = new Thread(session);
                    t.start();
                    NbrConnexion++;
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
            System.out.println( ioe );
        }
    }
    
    public static void main( String args[] )
    {
        ServeurEcho serveur = new ServeurEcho(args);
        serveur.Traitement();
    }
}