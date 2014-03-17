/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package serveurecho;

/**
 *
 * @author Isabelle
 */

import java.net.*;
import java.io.*;

class ServeurEcho
{
    //attributs
    int port = 7; //valeur par defaut
    final int NUMPORTMAX = 65535;
    final int MAXCONNEXION = 3;
    
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
    
    public static void main( String args[] )
    {
        try
        {
            ServerSocket serveur = new ServerSocket( 7 );
            System.out.println( "Serveur echo en ligne" );
            Socket client = serveur.accept();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader( client.getInputStream() ) );
            
            PrintWriter writer = new PrintWriter(
                new OutputStreamWriter( client.getOutputStream() ) );
            
            System.out.println( "Ouverture d'une connexion" );
            writer.println( "Bonjour! Bienvenue sur le serveur echo.\r" );
            writer.println( "Entrez \"Q\" pour quitter.\r" );
            writer.flush();
            boolean fini = false;
            while ( ! fini )
            {
                String ligne = reader.readLine();
                writer.println( "Echo: " + ligne );
                writer.flush();
                if( ligne.trim().equals( "Q" ) )
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
}