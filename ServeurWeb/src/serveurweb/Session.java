// Session.java
// Fait par : Simon Boucahard et Isabelle Angrignon
// Fait le 2014-03-17
// Gestion des sessions des utilisateurs du serveur echoes
package serveurweb;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.net.*;
import java.io.*;

public class Session implements Runnable
{
    BufferedReader reader;
    PrintWriter writer;
    Socket client;
    int NumSession = 0;
    final String PROMPT = "=>";
    String acceuil = "Magnifique serveur Web de Isabelle Angrignon et Simon Bouchard - version 1.0";
    final String PATHREP = "C:\\FichiersBidons";
    
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
    private void afficherListe(String rep)
    {
        File repertoire = new File(rep);
        String[] fichiers = repertoire.list();
        
        if (fichiers != null)
        {
            for (int i = 0; i < fichiers.length; i++)
            {
                writer.println(fichiers[i]);
            }
            writer.println(fichiers.length + " fichier(s) disponible(s)");
            writer.print(PROMPT);
            writer.flush();
        }
        
    }
    
    
    public void run ()
    {
        boolean fini = false;
        try
        {
            writer.println(acceuil);
            afficherListe("C:\\FichiersBidons");
            while ( ! fini )
            {
                String ligne = reader.readLine();
                if (ligne != null )
                {
                    TraitementRequete(ligne);
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
    
    private void TraitementRequete (String ligne)
    {
        String[] laCommande = ligne.split(" ");
        if ( laCommande.length  > 0 )
        {
            switch (laCommande[0].toUpperCase())
            {
                
                case "GET":
                    if (laCommande.length == 2 )
                    {
                        getFichier(laCommande[1]);
                    }
                    else
                    {
                        writer.println("400");
                    }
                    try
                    {
                        client.close();
                    }catch(IOException ioe) {  }
                    break;
                    
                    
                default:
                    writer.println("La commande : " + laCommande[0] + " n'existe pas");
            }
            
        }
        else
        {
            writer.println("Il n'y a aucun parametre");
        }
        writer.print(PROMPT);
        writer.flush();
    }
    private void getFichier(String nomFichier)
    {
        String path = PATHREP + "\\" + nomFichier;
        if(validerFichier(path))
        {
            File fichier = new File(path);
            String s = "";
            boolean pasFini = true;
            try
            {
                BufferedReader bufferRead = new BufferedReader(new FileReader(fichier));
                while (pasFini)
                {
                    s = bufferRead.readLine();
                    if(s != null)
                    {
                        writer.println(s);
                    }
                    else
                    {
                        pasFini = false;
                    }
                }
                bufferRead.close();
            }
            catch(IOException e) { e.printStackTrace(); }
            
        }
    }
    
    
    private boolean validerFichier(String nom)
    {
        File fichier = new File(nom);
        boolean existe = false;
        if (fichier.exists())
        {
            writer.println("200");
            existe = true;
        }
        else
        {
            writer.println("404");
        }
        return existe;
    }
}




