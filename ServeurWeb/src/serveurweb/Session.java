// Session.java
// Fait par : Simon Boucahard et Isabelle Angrignon
// Fait le 2014-03-17
// Gestion des sessions des utilisateurs du serveur echoes
package serveurweb;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.*;
import java.io.*;

public class Session implements Runnable
{
    BufferedReader reader;
    PrintWriter writer;
    Socket client;
    int NumSession = 0;
    String acceuil = "Magnifique serveur Web de Isabelle Angrignon et Simon Bouchard - version 1.0";
    
    public Session(Socket client , int NumeroSession)
    {
        try
        {
            this.client = client;
            reader = new BufferedReader(
                    new InputStreamReader( client.getInputStream() ) );
            writer = new PrintWriter(
                    new OutputStreamWriter( client.getOutputStream() ),true );
            this.NumSession = NumeroSession;
        }
        catch(IOException ioe)
        { 
            System.out.println("On est dans marde");
        }
    }
    
    public void run ()
    {
        boolean fini = false;
        try
        {
            writer.println(acceuil);
            while ( ! fini )
            {
                String ligne = reader.readLine();
                if (ligne != null )
                {
                    writer.println( ligne );
                    if( ligne.trim().equalsIgnoreCase("Q" ) )
                    {
                        fini = true;
                    }
                }
                else
                {
                    System.out.println("Fermeture imprévue de session " + NumSession);
                    fini = true;
                }
            }
            System.out.println("Fermeture de session " + NumSession );
        }
        catch(IOException ioe)
        { 
            System.out.println("Fermeture imprevue de session " + NumSession);
        }
        finally
        {
            ServeurWeb.NbrConnexion--;
            try
            {
                client.close();
            }catch(IOException ioe) {  }
        }        
    }    
}
