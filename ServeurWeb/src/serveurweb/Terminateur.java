// Nom: Terminateur.java
// Par: Isabelle Angrignon
// Date: 17-02-2014


// But: 
/*Vous devez écrire la classe Terminateur (fichier Terminateur.java), 
 *dont le code se résume à un thread qui, une fois lancé par un autre thread 
 *(dans notre cas, le programme de test décrit plus loin), lit continuellement (ligne par ligne)
 * ce qui est entré au clavier et se termine lorsque la ligne lue contient 
 *uniquement le caractére "Q" ou "q", avec ou sans espaces vides avant ou après.
 *Il n'y a donc pas d'appel à System.exit dans le thread terminateur. 
 **/
package serveurweb;

import java.io.*;

class Terminateur implements Runnable {
	public void run() {
		String s = "";
		try {
			BufferedReader bufferRead = new BufferedReader(
					new InputStreamReader(System.in));
			while (!s.trim().equalsIgnoreCase("q")) { //tant qu'on n'a pas seulement un q sur la ligne,...
				s = bufferRead.readLine();			  // on lit une ligne.
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

