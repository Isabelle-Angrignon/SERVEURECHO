// Session.java
// Fait par : Simon Bouchard et Isabelle Angrignon
// Fait le 2014-03-17
// Gestion des sessions des utilisateurs du serveur echoes
package serveurweb;

import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;

public class Session implements Runnable
{
    BufferedReader reader;  // Flux de texte 
    PrintWriter writer;     // Flux de texte
    Socket client;          // Le client passé par le serveur
    int NumSession = 0;     // Pour fin de suivi des connections
    Configuration maConf;   // contient racine, index, listage
 //   String pathRep = "C:\\www";     // Path des fichier a télécherger
    final String NOMSERVEUR = "ServeurWeb IA & SB";
    final String PROTOCOLE = "HTTP/1.0";
        
    //Messages validation de fichiers:
    final String FICHIERTROUVE = "200 Okay";                
    final String ERREURREQUETE = "400 Requete erronee";     //|--------------------|//
    final String INTERDIT =      "403 Interdit";            //| Message d'erreurs  |//
    final String FICHIERNONTROUVE = "404 Non trouve";       //|                    |//    
    final String PASIMPLEMENTE = "501 Non implemente";      //|--------------------|//
           
    // Constructeur
    public Session(Socket client , int NumeroSession , Configuration maConf)
    {
        this.maConf = maConf;    // Le path est bon on le sait puisque le serveur l'a vérifié
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
    // Affiche la liste de fichier a télécharger
    private void afficherPageRepertoire(String rep)
    {
        String nomRep = rep;//nomRep est utilisé pour l'affichage sur l page web seulement
        if (rep.equals("/"))
        {
            nomRep = "";
        }
        writer.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
        writer.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        writer.println("<head>");
        writer.println("<meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\" />");
        writer.println("<title>Path Index</title>");
        writer.println(" </head>");
        writer.println("<body>");
        writer.println("<h1>Contenu du repertoire " +maConf.getRacine()+nomRep + "</h1>");
        writer.println("<p>");
        
        File repertoire = new File(maConf.getRacine()+rep);
        String[] fichiers = repertoire.list();                        ///////////////////////////////////////////////
        if (fichiers != null)                                                   //       Pour chaque fichier dans le 
        {                                                                       //       path on appel la méthode afficher
            for (String fichier:fichiers)                                       //       info qui affichera de facon structurer 
            {                                                                   //       le fichier
                afficherInfos(rep, fichier);                                    //
                  writer.println("</br>");
            }                                                                   //
            writer.println(fichiers.length + " fichier(s) disponible(s)");      ////////////////////////////////////////////////
        }
        
        writer.println("</p>");
        writer.println("</body>");
        writer.println("</html>");
        
    }
    
    // Sert a structurer la facon d'afficher les informations relier sur le fichier
    private void afficherInfos(String path, String fichier)
    {
        File f = new File(maConf.getRacine() + "\\" +path + "\\" + fichier);
        if(!path.equalsIgnoreCase("/") && !path.equalsIgnoreCase("\\") )
        {
            path += "/";
        }
        writer.println("<a href=\""+ path + f.getName() +"\">");
        if (!f.isDirectory())
        {
            //writer.printf("%-30s %10s %tD %n", "    " + fichier, f.length(), f.lastModified()); // Utilise printf vielle méthode du c
            writer.print("    ");
            writer.print(f.getName());
            writer.print("        ");
            writer.print(fichier + "     "+ f.length() + "      " + f.lastModified()); // Utilise printf vielle méthode du c
            writer.println();
        }
        else
        {
            writer.printf("%-41s %tD %n", " [ ]" + fichier, f.lastModified());      // Utilise printf vielle méthode du c
        }
        writer.println("</a>");
    }
    
    public void run ()
    {
        boolean fini = false;
        try
        {
            // recevoir requete  
            String maLigne;
            String ligne = maLigne = reader.readLine();
            //Consommer l'entête du browser
            while (!ligne.equals(""))
            {
                ligne = reader.readLine();
            }
            //envoi page
            TraitementRequete(maLigne);            
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
                reader.close();
                client.close();
            }
            catch(IOException ioe) {  }
        }
    }
    
    private void afficherPageErreur(String erreur)
    {        
        File fichierErreur;        
        switch (erreur)
        {
            case "ERREURREQUETE"://400
                fichierErreur = new File("FichierServeur/400_Serveur_web.html");
                break;
            case "INTERDIT"://403
                fichierErreur = new File("FichierServeur/403_Serveur_web.html");
                break;
            case "FICHIERNONTROUVE"://404
                fichierErreur = new File("FichierServeur/404_Serveur_web.html");
                break;
            case "PASIMPLEMENTE"://501
                fichierErreur = new File("FichierServeur/501_Serveur_web.html");
                break; 
            default:
                fichierErreur = new File("FichierServeur/404_Serveur_web.html");
                break;
        } 
        writer.println(PROTOCOLE + " " + erreur); 
        genererEntete(fichierErreur);
        traiterFichier(fichierErreur);
    }
    
    private void TraitementRequete (String ligne)
    {
        String[] laCommande = ligne.trim().split("\\s+");
        
        if ( laCommande.length  > 0 )
        {
            switch (laCommande[0].toUpperCase())
            {                
                case "GET":
                    if (laCommande.length == 3 )
                    {
                        traiterRequeteGet(laCommande[1]);                        
                    }
                    else
                    {                                           
                        afficherPageErreur(ERREURREQUETE);
                    }
                    try
                    {
                        client.close();
                    }catch(IOException ioe) {  }
                    break;
                case "HEAD":
                    if (laCommande.length == 3 )
                    {
                        traiterRequeteHead(laCommande[1]);                        
                    }
                    else
                    {
                        writer.println(PROTOCOLE + " " + ERREURREQUETE);
                    }
                    try
                    {
                        client.close();
                    }catch(IOException ioe) {  }
                    break;    
                    
                    
                default:
                    afficherPageErreur(PASIMPLEMENTE);
                    try { client.close(); }catch(IOException ioe) {  }
            }
        }
        else
        {
            afficherPageErreur(ERREURREQUETE);
            try { client.close(); }catch(IOException ioe) {  }
        }        
    }
    
    private void traiterRequeteGet(String nomFichier)
    {
        String path = maConf.getRacine() + nomFichier;
        if(validerFichier(path))
        {
            File fichier = new File(path);            
            if ( !fichier.isDirectory())
            {   
                genererEntete(fichier);//ajouter un head
                traiterFichier(fichier);
            }
            else
            {   //generer entete est géré par traiter dossier                             
                traiterDossier(nomFichier);                
            }
        }
        else
        {
            afficherPageErreur(FICHIERNONTROUVE);
        }
    }
    private void traiterRequeteHead(String nomFichier)
    {
        String path = maConf.getRacine() + "\\"+nomFichier;
        if(validerFichier(path))
        {
            File fichier = new File(path);
            genererEntete(fichier);//ajouter un head            
        }
        else
        {
            writer.println(PROTOCOLE + " " + FICHIERNONTROUVE);             
        }
    }
            
    //cadeau du prof....
    private String getDateRfc822(Date date)
    {
       SimpleDateFormat formatRfc822
          = new SimpleDateFormat( "EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z",
             Locale.US );

       return formatRfc822.format( date );
    }
    private String getType(String extension)
    {          
        switch(extension)
        {
            case "gif":
                return "image/gif";
            case "html":
                return "text/html";
            case "jpeg":
                return "image/jpeg";
            case "jpg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "txt":
                return "text/plain";
            default:
                return "";////?
        }        
    }
    
    private void genererEntete(File fichier)
    {      
        String type ="";
        //PROTOCOLE + message réussite  AFFICHÉ PLUS HAUT
        Date dateM = new Date(fichier.lastModified());
        if(fichier.isFile())
        {
            String extension = (fichier.getName().split("\\."))[1];
            type = getType(extension);
        }
        
        Date dateJ = new Date();
                
        writer.println("Server: " + NOMSERVEUR);
        writer.println("Date: "+ getDateRfc822(dateJ));
        //Si le type n'est pas géré, la ligne sera omise...
        if (type.equals(""))
        {
            //il s'agit d'un dossier
            writer.println("Content-Type: " + "text/HTML");
        }
        else
        {
            writer.println("Content-Type: " + type);
        }
        writer.println("Last-Modified: " + getDateRfc822(dateM));
        if(fichier.length() != 0 )
        {
            writer.println("Content-Length: " + fichier.length());
        }
        
        writer.println();
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
    
    private void traiterDossier(String rep)
    {
      
        File index = new File(maConf.getRacine() +"\\" + rep + "\\" + maConf.getNomIndex());
        //serie de checks

        //si index.html existe
        if ((index.isFile()))
        {
            //afficher index.html            
            genererEntete(index);
            traiterFichier(index);
        }
        else
        {
            //sinon si listage = false, 
            if (!maConf.getListage())
            {
                //afficher page 403
                //générerEntete est géré par afficher page d'erreur
                afficherPageErreur(INTERDIT);
            }
            else
            {
                //sinon générer liste.
                genererEntete(index);
                afficherPageRepertoire(rep);                
            }
        }
        
    }
    
    private boolean validerFichier(String nom)
    {
        File fichier = new File(nom);
        boolean existe = false;
        if (fichier.exists())
        {
            writer.println(PROTOCOLE + " " + FICHIERTROUVE);
            existe = true;
        }       
        return existe;
    }
}




