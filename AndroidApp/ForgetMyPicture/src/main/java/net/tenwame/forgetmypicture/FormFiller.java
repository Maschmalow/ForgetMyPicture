package net.tenwame.forgetmypicture;

import android.util.Log;

import net.tenwame.forgetmypicture.UserData;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class FormFiller {
    private static final String TAG = FormFiller.class.getSimpleName();
    
    private FormFiller(){}

    public static int fillForm() {

        // Le HTTPMethod qui sera un Post en lui indiquant l’URL du traitement du formulaire (celle qui se trouve normalement après le "action="
        Connection methode = Jsoup.connect("https://support.google.com/legal/contact/lr_eudpa?product=websearch&hl=en").method(Connection.Method.POST);
        //UserData.getInstance().
        // On ajoute les parametres du formulaire, aller chercher les infos du client, il faudra mettre les bonnes valeurs
        methode.data("selected_country", "France"/*UserData.getInstance().*/); // (champs, valeur)
        methode.data("name_searched", UserData.getInstance().getForename() + " " + UserData.getInstance().getName());
        methode.data("requestor_name", UserData.getInstance().getForename() + " " + UserData.getInstance().getName());
        methode.data("contact_email_noprefill", UserData.getInstance().getEmail());

        // Ici c'est pour les URLs, il faudra faire qqc de particulier
        methode.data("url_box3", "http://premiereadereferencer.com");
        methode.data("url_box3", "http://deuxiemeadereferencer.fr");

        methode.data("eudpa_explain", "Cette URL me concerne, car…");
        methode.data("legal_idupload", "carte_identitee.png");
        methode.data("eudpa_consent_statement", "agree");
        methode.data("signature", UserData.getInstance().getForename() + " " + UserData.getInstance().getName());
        methode.data("signature_date", "01/30/2016"); // ex : "01/30/2016"


        int retour = -1;

        try
        {
            Connection.Response response = methode.execute();
            retour = response.statusCode();

                    // Pour la gestion des erreurs ou un debuggage, on recupere le nombre renvoye.
            Log.i(TAG, "La reponse est : " + retour );
            Log.i(TAG, response.body());


        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }


        return retour;
    }
}
