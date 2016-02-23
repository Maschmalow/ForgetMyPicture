import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Envoi {

    public static void main(String args[]) {
	// On cree le client
	HttpClient client = new HttpClient();

	// Le HTTPMethod qui sera un Post en lui indiquant l’URL du traitement du formulaire
	PostMethod methode = new PostMethod(“http://www.pexiweb.be/demo/form/traitement.php”);
					    
	// On ajoute les parametres du formulaire
	methode.addParameter(“champ”, “Hello World !”); // (champs, valeur)

	// Le buffer qui nous servira a recuperer le code de la page
	BufferedReader br = null;

	try
	    {
		// http://hc.apache.org/httpclient-3.x/apidocs/org/apache/commons/httpclient/HttpStatus.html
		int retour = client.executeMethod(methode);

		// Pour la gestion des erreurs ou un debuggage, on recupere le nombre renvoye.
		System.out.println(“La reponse de executeMethod est : ” + retour);

		br = new BufferedReader(new InputStreamReader(methode.getResponseBodyAsStream()));
		String readLine;

		// Tant que la ligne en cours n’est pas vide
		while(((readLine = br.readLine()) != null))
		    {
			System.out.println(readLine);
		    }
	    }

	catch (Exception e)
	    {
		System.err.println(e); // erreur possible de executeMethod
	    }

	finally
	    {
		// On ferme la connexion
		methode.releaseConnection();

		if(br != null)
		    {
			try
			    {
				br.close(); // on ferme le buffer
			    }

			catch (Exception e) { /* on fait rien */ }
		    }
	    }
    }   
}
