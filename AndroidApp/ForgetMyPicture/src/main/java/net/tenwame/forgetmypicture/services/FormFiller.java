package net.tenwame.forgetmypicture.services;

import android.os.Bundle;

import net.tenwame.forgetmypicture.ForgetMyPictureApp;
import net.tenwame.forgetmypicture.UserData;
import net.tenwame.forgetmypicture.database.Request;
import net.tenwame.forgetmypicture.database.Result;
import net.tenwame.forgetmypicture.database.User;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;

public class FormFiller extends NetworkService{
    private static final String TAG = FormFiller.class.getSimpleName();

    public static final String EXTRA_REQUEST_ID_KEY = ForgetMyPictureApp.getName() + ".requestId";

    public static final String ACTION_FILL_FORM = ForgetMyPictureApp.getName() + ".fill_form";

    public static final String FORM_URL = "https://support.google.com/legal/contact/lr_eudpa?product=websearch&hl=en";
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
            if(!request.getStatus().isAfter(Request.Status.PENDING))
                throw new RuntimeException("Invalid request status: " + request.getStatus());


            Connection connection = Jsoup.connect("127.0.0.1");

            User user = UserData.getUser();
            connection.data("selected_country", "France"); // (champs, valeur)
            connection.data("name_searched", user.getForename() + " " + user.getName());
            connection.data("requestor_name", user.getForename() + " " + user.getName());
            connection.data("contact_email_noprefill", user.getEmail());

            for( Result result : request.getResults())
                connection.data("url_box3", result.getPicURL());

            connection.data("eudpa_explain", request.getMotive());
            connection.data("eudpa_consent_statement", "agree");
            connection.data("signature", user.getForename() + " " + user.getName());
            connection.data("signature_date", DateFormat.getDateTimeInstance().format(new Date()));

            InputStream stream = user.getIdCard().openStream();
            connection.data("legal_idupload", "carte identite", stream);
            connection.post();

            stream.close();
        }

    };

    private Request getRequest(Bundle params) throws Exception{
        int requestId = params.getInt(EXTRA_REQUEST_ID_KEY, -1);
        Request request = ForgetMyPictureApp.getHelper().getRequestDao().queryForId(requestId);
        if(request == null)
            throw new IllegalArgumentException("Missing or invalid request: Id " + requestId);

        return request;
    }


}
