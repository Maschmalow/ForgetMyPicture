package net.tenwame.forgetmypicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings.Secure;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Antoine on 20/02/2016.
 * class used as a gateway to the user personal data
 */
public class UserData {
    private static final String TAG = UserData.class.getSimpleName();

    private static final String SELFIE_SUFFIX = "_selfie";
    private static final String IDCARD_SUFFIX = "_idcard";
    private Context context = ForgetMyPictureApp.getAppContext();
    private final String deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

    private static UserData instance = null;
    public static UserData getInstance() {
        if(instance == null)
            synchronized (UserData.class) {
                if(instance == null)
                    instance = new UserData();
            }

        return instance;
    }

    private boolean userIsNotSet = false;

    private Bitmap idCard;
    private Bitmap selfie;
    private String name;
    private String forename;
    private String email;


    private UserData() {
        loadFromFile();
    }

    public boolean isSet() {
        return !userIsNotSet;
    }

    public void setupUserData(Bitmap idCard, Bitmap selfie, String name, String forename, String email) {
        if(isSet())
            throw new RuntimeException("User data is already set");

        this.idCard = idCard;
        this.selfie = selfie;
        this.name = name;
        this.forename = forename;
        this.email = email;

        if(!isDataValid())
            return;  //isSet() == false

        try {
            storeToFile();
        } catch (IOException e) {
            throw new RuntimeException("Could not save user data");
        }

        //isSet() == true
    }

    private void loadFromFile() {
        FileInputStream dataFile;

        try {
            dataFile = context.openFileInput(deviceId);
        } catch (FileNotFoundException e) {
            userIsNotSet = true;
            return;
        }

        try {
            JsonReader reader = new JsonReader(new FileReader(dataFile.getFD()));
            reader.beginObject();
            while (reader.hasNext()) {
                setUserProperty(reader.nextName(), reader.nextString());
            }
            reader.endObject();
            reader.close();

            dataFile = context.openFileInput(deviceId + IDCARD_SUFFIX);
            idCard = BitmapFactory.decodeStream(dataFile);
            dataFile.close();

            dataFile = context.openFileInput(deviceId + SELFIE_SUFFIX);
            selfie = BitmapFactory.decodeStream(dataFile);
            dataFile.close();

        } catch(IOException | IllegalArgumentException e) {
            wipe();
            throw new RuntimeException("User data is incorrectly stored", e);
        }

        if(!isDataValid()) {
            wipe();
            throw new RuntimeException("User data is not valid");
        }
        Log.i(TAG, "Parsed data from file");

    }

    private void setUserProperty(String property, String value) {
        if("name".equals(property))
            name = value;
        else if("forename".equals(property))
            forename = value;
        else if("email".equals(property))
            email = value;
        else
            throw new IllegalArgumentException("Property " + property + " does not exists.");
    }

    private void storeToFile() throws IOException{

        FileOutputStream dataFile;
        try {
            dataFile = context.openFileOutput(deviceId, Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new FileWriter(dataFile.getFD()));
            writer.beginObject();
            writer.name("name");
            writer.value(name);
            writer.name("forename");
            writer.value(forename);
            writer.name("email");
            writer.value(email);
            writer.endObject();
            writer.close();

            dataFile = context.openFileOutput(deviceId + SELFIE_SUFFIX, Context.MODE_PRIVATE);
            selfie.compress(Bitmap.CompressFormat.PNG, 0, dataFile);
            dataFile.close();

            dataFile = context.openFileOutput(deviceId + IDCARD_SUFFIX, Context.MODE_PRIVATE);
            idCard.compress(Bitmap.CompressFormat.PNG, 0, dataFile);
            dataFile.close();

        } catch (IOException e) {
            wipe();
            throw e;
        }

        userIsNotSet = false;
        Log.i(TAG, "Stored data to file");
    }

    private boolean isDataValid() { //TODO: also check for non-null values validity
        return selfie != null && idCard != null && name != null && forename != null && email != null;
    }

    public void wipe() {
        context.deleteFile(deviceId);
        context.deleteFile(deviceId + IDCARD_SUFFIX);
        context.deleteFile(deviceId + SELFIE_SUFFIX);
        selfie = idCard = null;
        name = forename = email = null;

        userIsNotSet = true;
        Log.w(TAG, "User data wiped");

    }

    public Bitmap getIdCard() {
        return idCard;
    }

    public Bitmap getSelfie() {
        return selfie;
    }

    public String getName() {
        return name;
    }

    public String getForename() {
        return forename;
    }

    public String getEmail() {
        return email;
    }

}
