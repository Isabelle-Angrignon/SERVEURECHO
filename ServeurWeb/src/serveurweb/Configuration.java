/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package serveurweb;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.net.*;
import java.io.*;


public class Configuration 
{
    private int port = 80;
    private boolean listage = false;
    private String index = "Index.html";
    private String racine = "c:\\www";
    final int NUMPORTMAX = 65535;

    public Configuration() 
    {
        
    }
    public Configuration(int port , String racine , String index , String listage) throws Exception
    {
        setPort(port);
        setRacine(racine);
        setIndex(index);
        setListage(listage);
    }
    public Configuration(int port , String racine , String index , boolean listage) throws Exception
    {
        setPort(port);
        setRacine(racine);
        setIndex(index);
        setListage(listage);
    }
    public Configuration(String port , String racine , String index , String listage) throws Exception
    {
        setPort(port);
        setRacine(racine);
        setIndex(index);
        setListage(listage);
    }
    public Configuration(String port , String racine , String index , boolean listage) throws Exception
    {
        setPort(port);
        setRacine(racine);
        setIndex(index);
        setListage(listage);
    }
    
    public void setListage (String listage ) throws Exception
    {
        if (listage.equalsIgnoreCase("oui"))        { setListage(true);}
        else if (listage.equalsIgnoreCase("non") )  {setListage(false);}
        else                                        {throw new Exception("config.txt incorecte seul oui ou non est accepter pour le parametre listage");}
    }
    public void setListage(boolean listage)
    {
        this.listage = listage;
    }
    public void setPort (int port) throws Exception
    {
        if(port < NUMPORTMAX && port > 0 )  // Si le numerot de port est hors limie
        {
            this.port = port;
        }
        else
        {
            throw new Exception("Le numero de port est hors des bornes de 0 et " + NUMPORTMAX);
        }
    }
    public void setPort (String port ) throws Exception
    {
        try
        {
            int portInt = Integer.parseInt(port);
            setPort(portInt);
        }
        catch (Exception e) {throw new Exception("Le port n'est pas un numero");}
    }
    public void setRacine(String racine) throws Exception
    {
        if (!(new File(racine).isDirectory())) // Si le dossiern'existe pas 
        {
            throw new Exception("Le serveur a tente de se lancer sur le repertoire par defaut "+ racine+" mais ce repertoire n'existe pas !" );
        }
        else
        {
            this.racine=racine;
        }
    }
    public void setIndex(String Index)
    {
        this.index = Index;
    }
    public String getRacine()   {return this.racine;    }
    public String getNomIndex() {return this.racine;    }
    public int getPort()        {return this.port;      }
    public boolean getListage() {return this.listage;   }
    
    
    
}
