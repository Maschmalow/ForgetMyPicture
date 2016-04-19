package net.tenwame.forgetmypicture.services;

import android.os.Bundle;
import android.util.Log;

import net.tenwame.forgetmypicture.ForgetMyPictureApp;
import net.tenwame.forgetmypicture.UserData;
import net.tenwame.forgetmypicture.database.Request;
import net.tenwame.forgetmypicture.database.Result;
import net.tenwame.forgetmypicture.database.User;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.InputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;

public class FormFiller extends NetworkService{
    private static final String TAG = FormFiller.class.getSimpleName();

    public static final String EXTRA_REQUEST_ID_KEY = ForgetMyPictureApp.getName() + ".requestId";

    public static final String ACTION_FILL_FORM = ForgetMyPictureApp.getName() + ".fill_form";

    private static final int HTTP_OK = 200;

    
    public FormFiller(){
        super(TAG);
        handlers.put(ACTION_FILL_FORM, fillForm);
    }


    public static void execute(Request request) {
        Bundle params = new Bundle();
        params.putInt(EXTRA_REQUEST_ID_KEY, request.getId());
        execute(FormFiller.class, ACTION_FILL_FORM, params);
    }


    private ActionHandler fillForm = new ActionHandler() {
        @Override
        public void handle(Bundle params) throws Exception {
            Request request = getRequest(params);
            //pending because the work isn't done unless we did fill the form
            if(request.getStatus() != Request.Status.PENDING) {
                Log.e(TAG, "Request is not pending (status=" + request.getStatus() + ")");
                fail();
            }

            Connection method = Jsoup.connect("https://support.google.com/legal/contact/lr_eudpa?product=websearch&hl=en").method(Connection.Method.POST);

            User user = UserData.getUser();
            method.data("selected_country", "France"); // (champs, valeur)
            method.data("name_searched", user.getForename() + " " + user.getName());
            method.data("requestor_name", user.getForename() + " " + user.getName());
            method.data("contact_email_noprefill", user.getEmail());

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
            }
            catch (Exception e) {
                Log.e(TAG, "Request failed");
                fail();
                return; //only to remove warning
            }

            if(code == HTTP_OK)
                request.setStatus(Request.Status.FINISHED);
            else {
                Log.e(TAG, "Request failed with HTTP code " + code);
                fail();
            }
        }

    };

    private Request getRequest(Bundle params) {
        int requestId = params.getInt(EXTRA_REQUEST_ID_KEY, -1);
        Request request = null;
        try {
            request = ForgetMyPictureApp.getHelper().getRequestDao().queryForId(requestId);
        } catch (SQLException e) {
            Log.e(TAG, "SQL error", e);
            fail();
        }
        if(request == null) {
            Log.e(TAG, "No request found for request Id: " + requestId);
            fail();
        }
        return request;
    }


}
