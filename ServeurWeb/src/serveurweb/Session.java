// Session.java
// Fait par : Simon Boucahard et Isabelle Angrignon
// Fait le 2014-03-17
// Gestion des sessions des utilisateurs du serveur echoes
package serveurweb;

import java.net.*;
import java.io.*;

public class Session implements Runnable
{
    BufferedReader reader;  // Flux de texte 
    PrintWriter writer;     // Flux de texte
    Socket client;          // Le client passer par le serveur
    int NumSession = 0;     
    final int DELAI = 20000; //délai pour entrer la commande sinon fermeture
    final String PROMPT = "=>";
    String accueil = "Magnifique serveur Web de Isabelle Angrignon et Simon Bouchard - version 1.0";
    String pathRep = "C:\\www";     // Path des fichier a télécherger
    //Messages validation de fichiers:
    final String FICHIERTROUVE = "200 Okay";                //|--------------------|//
    final String ERREURREQUETE = "400 Requete eronee";      //| Message d'érreure  |//
    final String PASIMPLEMENTE = "501 Non implemente";      //|                    |//
    final String FICHIERNONTROUVE = "404 Non trouve";       //|--------------------|//
    
    
    // Constructeur
    public Session(Socket client , int NumeroSession , String path)
    {
        this.pathRep = path;    // Le path est bon on le sait puisque le serveur l'a vérifié
        try
        {
            this.client = client;                                               ////////////////////////////////////////////////
            reader = new BufferedReader(                                        //    On établie la connexion entre la session
                    new InputStreamReader( client.getInputStream() ) );         //      et le client , on établi aussi les 
            writer = new PrintWriter(                                           //      flux de texte. les flux binaires     
                    new OutputStreamWriter( client.getOutputStream() ),true );  //      seront instauré plus tard
            this.NumSession = NumeroSession;                                    ////////////////////////////////////////////////
        }
        catch(IOException ioe)
        {
            System.out.println("On est dans marde");
        }
    }
    
    // Affiche la liste de fichier a télécharger
    private void afficherListe(String rep)
    {
        File repertoire = new File(rep);
        String[] fichiers = repertoire.list();
        writer.println("Contenu du repertoire " + rep);                         ///////////////////////////////////////////////
        if (fichiers != null)                                                   //       Pour chaque fichier dans le 
        {                                                                       //       path on appel la méthode afficher
            for (String fichier:fichiers)                                       //       info qui affichera de facon structurer 
            {                                                                   //       le fichier
                afficherInfos(rep, fichier);                                    //
            }                                                                   //
            writer.println(fichiers.length + " fichier(s) disponible(s)");      ////////////////////////////////////////////////
        }
    }
    
    // Sert a structurer la facon d'afficher les informations relier sur le fichier
    private void afficherInfos(String path, String fichier)
    {
        File f = new File(path + "\\" + fichier);
        if (!f.isDirectory())
        {
            writer.printf("%-30s %10s %tD %n", "    " + fichier, f.length(), f.lastModified()); // Utilise printf vielle méthode du c
        }
        else
        {
            writer.printf("%-41s %tD %n", " [ ]" + fichier, f.lastModified());      // Utilise printf vielle méthode du c
        }
    }
    
    public void run ()
    {
        boolean fini = false;
        try
        {
            writer.println(accueil);
            afficherListe(pathRep);
            writer.print(PROMPT);
            writer.flush();//autoflush ne marche pas sur les print sans ln
            
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
            }
            catch(IOException ioe) {  }
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
                        traiterRequeteGet(laCommande[1]);
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
                    writer.println(PASIMPLEMENTE);
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
    private void traiterRequeteGet(String nomFichier)
    {
        String path = pathRep + "\\" + nomFichier;
        if(validerFichier(path))
        {
            File fichier = new File(path);
            if ( !fichier.isDirectory())
            {
                traiterFichier(fichier);
            }
            else
            {
                afficherListe(path);
            }
        }
    }
    private void traiterFichier (File  fichier)
    {
            int b = -1;
            boolean pasFini = true;
            try
            {
                //transfert en binaire
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(fichier));
                BufferedOutputStream out = new BufferedOutputStream(client.getOutputStream());
                while (pasFini)
                {
                    b = in.read();
                    if(b != -1)
                    {
                        out.write(b);
                    }
                    else
                    {
                        pasFini = false;
                    }
                }
                in.close();
                out.close();
            }
            catch(IOException e) { e.printStackTrace(); }
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




