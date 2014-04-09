// Session.java
// Fait par : Simon Boucahard et Isabelle Angrignon
// Fait le 2014-03-17
// Gestion des sessions des utilisateurs du serveur echoes
package serveurweb;

import java.net.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Session implements Runnable
{
    BufferedReader reader;
    PrintWriter writer;
    Socket client;
    int NumSession = 0;
    final int DELAI = 20000; //délai pour entrer la commande sinon fermeture
    final String PROMPT = "=>";
    String accueil = "Magnifique serveur Web de Isabelle Angrignon et Simon Bouchard - version 1.0";
    String pathRep = "C:\\www";
    //Messages validation de fichiers:
    final int FICHIERTROUVE = 200;
    final int ERREURREQUETE = 400;
    final int FICHIERNONTROUVE = 404;
    
    
    
    public Session(Socket client , int NumeroSession , String path)
    {
        this.pathRep = path;
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
            for (String fichier:fichiers)
            {   
                afficherInfos(pathRep, fichier);
            }
            writer.println(fichiers.length + " fichier(s) disponible(s)");
            writer.print(PROMPT);
            writer.flush();//autoflush ne marche pas sur les print sans ln
        }        
    } 
    
    private String formaterDate(long d)
    {       
        SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
        return date.format(new Date(d));
    }
    
    private void afficherInfos(String path, String fichier)
    {
        File f = new File(path + "\\" + fichier);
    //    long date = f.lastModified();
        if (!f.isDirectory())
        {
   //         writer.print("    ");
            writer.printf("%-30s %10s %tD %n", "    " + fichier, f.length(), f.lastModified());
        }
        else 
        {
   //         writer.print(" [ ]");
            writer.printf("%-30s %10s %tD %n", " [ ]" + fichier, f.length(), f.lastModified());
   //         writer.printf("%-30s %-10s %tD %n", " [ ]" + f.getName(), f.length(), f.lastModified());
        }                
  //      writer.print(fichier);
  //      writer.format("%20s", f.length());
  //      writer.format("%35s", formaterDate(date));
  //      writer.println();
    }
    
    public void run ()
    {
        boolean fini = false;
        try
        {            
            writer.println(accueil);
            afficherListe(pathRep);
            
            client.setSoTimeout(DELAI);
            while ( ! fini )
            {
                String ligne = reader.readLine();
                if (ligne != null )
                {
                    TraitementRequete(ligne);
                }
            }            
        }
        catch( SocketTimeoutException ste )
        {
           writer.println("Votre delai de " + DELAI/1000 + " sec. est ecoule.");		   
        }
        catch(IOException ioe)
        {
            //Rien a faire
        }
        finally
        {
            ServeurWeb.NbrConnexion--;
			System.out.println("Fermeture de session " + NumSession);
            try
            {
                client.close();
            }catch(IOException ioe) {  }
        }
    }
    
    private void TraitementRequete (String ligne)
    {
        String[] laCommande = ligne.trim().split("\\s+");
        
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
                        writer.println(ERREURREQUETE);
                    }
                    try
                    {
                        client.close();
                    }catch(IOException ioe) {  }
                    break;
                    
                    
                default:
                    writer.println(ERREURREQUETE);
                    try { client.close(); }catch(IOException ioe) {  }
            }            
        }
        else
        {
            writer.println(ERREURREQUETE);
            try { client.close(); }catch(IOException ioe) {  }
        }
        writer.print(PROMPT);
        writer.flush();
    }
    private void getFichier(String nomFichier)
    {
        String path = pathRep + "\\" + nomFichier;
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
            writer.println(FICHIERTROUVE);
            existe = true;
        }
        else
        {
            writer.println(FICHIERNONTROUVE);
        }
        return existe;
    }
}




