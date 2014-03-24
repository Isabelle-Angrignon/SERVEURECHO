// Serveur echo 
// Fait par : Simon Bouchard et Isabelle Angrignon
// Fait le  : 2014-03-17
// Gestion d'un serveur écho ( Gestion des attributuions des sessions )





import java.net.*;
import java.io.*;

class ServeurEcho
{
    //attributs
    int port = 7; //valeur par defaut
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
        else
        {
          System.out.println("Nom de port invalide, le port "+ port + " sera donne au serveur");  
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
            catch (Exception e) { System.out.println("Numero de port non valide, fermeture du serveur"); }
        } 
        else if(tab.length > 1)
        {
            System.out.println("Nombre de parametre invalide le port : " + port + " sera donne au serveur");
        }
    }
    
    public void Traitement()
    {
        try
        {
            ServerSocket serveur = new ServerSocket( port );
            System.out.println( "Serveur echo en ligne. " );
            System.out.println("Ce serveur utilise le port : " + port);
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
            System.out.println("Port non disponible, le processus va maintenant s'arreter");
        }
    }
    
    public static void main( String args[] )
    {
        ServeurEcho serveur = new ServeurEcho(args);
        if ( serveur != null)
        {
            serveur.Traitement();
        }
    }
}