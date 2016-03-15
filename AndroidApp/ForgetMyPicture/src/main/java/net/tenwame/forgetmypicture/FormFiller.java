package net.tenwame.forgetmypicture;

import android.graphics.Bitmap;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.text.DateFormat;
import java.util.Date;

public class FormFiller {
    private static final String TAG = FormFiller.class.getSimpleName();
    
    private FormFiller(){}

    public static int fillForm(SearchData.Request request) {
        //pending because the work isn't done unless we did fill the form
        if(request.getStatus() != SearchData.Request.Status.PENDING) return -1;

        Connection method = Jsoup.connect("https://support.google.com/legal/contact/lr_eudpa?product=websearch&hl=en").method(Connection.Method.POST);


        method.data("selected_country", "France"); // (champs, valeur)
        method.data("name_searched", UserData.getInstance().getForename() + " " + UserData.getInstance().getName());
        method.data("requestor_name", UserData.getInstance().getForename() + " " + UserData.getInstance().getName());
        method.data("contact_email_noprefill", UserData.getInstance().getEmail());

        // Ici c'est pour les URLs, il faudra faire qqc de particulier$
        for( SearchService.Result result : request.getResults())
            method.data("url_box3", result.getPicURL());

        method.data("eudpa_explain", request.getMotive());

        method.data("legal_idupload", "carte identite", UserData.getInstance().getProperty("idCard", Bitmap.class).openStream());
        method.data("eudpa_consent_statement", "agree");
        method.data("signature", UserData.getInstance().getForename() + " " + UserData.getInstance().getName());
        method.data("signature_date", DateFormat.getDateTimeInstance().format(new Date()));

        int code;
        try
        {
            Connection.Response response = method.execute();
            code = response.statusCode();

            Log.i(TAG, "Status code: " + code );
            Log.i(TAG, response.body());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        request.setStatus(SearchData.Request.Status.FINISHED);
        return code;
    }
}
