// Session.java
// Fait par : Simon Boucahard et Isabelle Angrignon
// Fait le 2014-03-17
// Gestion des sessions des utilisateurs du serveur echoes
package serveurecho;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.*;
import java.io.*;

public class Session implements Runnable
{
    BufferedReader reader;
    PrintWriter writer;
    Socket client;
    public Session(Socket client)
    {
        try
        {
            this.client = client;
            reader = new BufferedReader(
                    new InputStreamReader( client.getInputStream() ) );
            writer = new PrintWriter(
                    new OutputStreamWriter( client.getOutputStream() ) );
        }
        catch(IOException ioe)
        { 
            System.out.println("On est dans marde");
        }
    }
    
    public void run ()
    {
        
    }
    
}
