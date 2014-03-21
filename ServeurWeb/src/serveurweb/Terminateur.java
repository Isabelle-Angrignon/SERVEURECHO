// Nom: Terminateur.java
// Par: Isabelle Angrignon
// Date: 17-02-2014
// But: 
/*Vous devez �crire la classe Terminateur (fichier Terminateur.java), 
 *dont le code se r�sume � un thread qui, une fois lanc� par un autre thread 
 *(dans notre cas, le programme de test d�crit plus loin), lit continuellement (ligne par ligne)
 * ce qui est entr� au clavier et se termine lorsque la ligne lue contient 
 *uniquement le caract�re "Q" ou "q", avec ou sans espaces vides avant ou apr�s.
 *Il n'y a donc pas d'appel � System.exit dans le thread terminateur. 
 **/
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
