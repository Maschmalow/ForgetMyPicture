package net.tenwame.forgetmypicture;

import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import net.tenwame.forgetmypicture.database.Request;
import net.tenwame.forgetmypicture.database.Result;
import net.tenwame.forgetmypicture.database.User;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.text.DateFormat;
import java.util.Date;

public class FormFiller {
    private static final String TAG = FormFiller.class.getSimpleName();
    
    private FormFiller(){}

    public static int fillForm(Request request) {
        //pending because the work isn't done unless we did fill the form
        if(request.getStatus() != Request.Status.PENDING) return -1;
        DatabaseHelper helper = OpenHelperManager.getHelper(ForgetMyPictureApp.getContext(), DatabaseHelper.class);

        Connection method = Jsoup.connect("https://support.google.com/legal/contact/lr_eudpa?product=websearch&hl=en").method(Connection.Method.POST);

        User user = UserData.getInstanceUser(helper);
        method.data("selected_country", "France"); // (champs, valeur)
        method.data("name_searched", user.getForename() + " " + user.getName());
        method.data("requestor_name", user.getForename() + " " + user.getName());
        method.data("contact_email_noprefill", user.getEmail());

        // Ici c'est pour les URLs, il faudra faire qqc de particulier$
        for( Result result : request.getResults())
            method.data("url_box3", result.getPicURL());

        method.data("eudpa_explain", request.getMotive());

        method.data("legal_idupload", "carte identite", user.getIdCard().openStream());
        method.data("eudpa_consent_statement", "agree");
        method.data("signature", user.getForename() + " " + user.getName());
        method.data("signature_date", DateFormat.getDateTimeInstance().format(new Date()));

        int code;
        try {
            Connection.Response response = method.execute();
            code = response.statusCode();

            Log.i(TAG, "Status code: " + code );
            Log.i(TAG, response.body());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        request.setStatus(Request.Status.FINISHED);

        OpenHelperManager.releaseHelper();
        return code;
    }
}
