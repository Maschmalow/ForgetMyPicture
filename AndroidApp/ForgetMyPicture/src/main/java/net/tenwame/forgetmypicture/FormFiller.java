package net.tenwame.forgetmypicture;

import android.os.AsyncTask;
import android.util.Log;

import net.tenwame.forgetmypicture.database.Request;
import net.tenwame.forgetmypicture.database.Result;
import net.tenwame.forgetmypicture.database.User;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;

public class FormFiller {
    private static final String TAG = FormFiller.class.getSimpleName();
    private static final int HTTP_OK = 200;
    
    private FormFiller(){}

    private static int fillFormASync(Request request) {
        //pending because the work isn't done unless we did fill the form
        if(request.getStatus() != Request.Status.PENDING) return -1;

        Connection method = Jsoup.connect("https://support.google.com/legal/contact/lr_eudpa?product=websearch&hl=en").method(Connection.Method.POST);

        User user = UserData.getUser();
        method.data("selected_country", "France"); // (champs, valeur)
        method.data("name_searched", user.getForename() + " " + user.getName());
        method.data("requestor_name", user.getForename() + " " + user.getName());
        method.data("contact_email_noprefill", user.getEmail());

        // Ici c'est pour les URLs, il faudra faire qqc de particulier
        for( Result result : request.getResults())
            method.data("url_box3", result.getPicURL());

        method.data("eudpa_explain", request.getMotive());
        method.data("eudpa_consent_statement", "agree");
        method.data("signature", user.getForename() + " " + user.getName());
        method.data("signature_date", DateFormat.getDateTimeInstance().format(new Date()));

        int code;
        try {
            InputStream stream = user.getIdCard().openStream();
            method.data("legal_idupload", "carte identite", stream);
            Connection.Response response = method.execute();
            code = response.statusCode();
            stream.close();

            Log.i(TAG, "Status code: " + code );
            Log.i(TAG, response.body());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(code == HTTP_OK)
            request.setStatus(Request.Status.FINISHED);

        return code;
    }

    public static void fillForm(final Request request) {
        Log.d(TAG, "Filling form for request " + request.getId());

        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return fillFormASync(request);
                } catch (Exception e) {
                    throw new RuntimeException("Error in fillForm()", e);
                }
            }
        }.execute((Void) null);
    }
}
