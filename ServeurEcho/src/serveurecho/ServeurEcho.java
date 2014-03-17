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
    
    void SetPort(int p)
    {
        if (p > 0 && p < NUMPORTMAX)
        {
            port = p;
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
            Socket client = serveur.accept();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader( client.getInputStream() ) );
            
            PrintWriter writer = new PrintWriter(
                new OutputStreamWriter( client.getOutputStream() ) );
            
            System.out.println( "Ouverture d'une connexion" );
           
            boolean fini = false;
            while ( ! fini )
            {
                String ligne = reader.readLine();
                writer.println( ligne );
                writer.flush();
                if( ligne.trim().equalsIgnoreCase("Q" ) )
                {
                    fini = true;
                }
            }
            System.out.println( "Fermeture d'une connexion" );
            client.close();
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