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
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Antoine on 20/02/2016.
 * class used as a gateway to the user personal data
 * //TODO: big refactoring
 */
public class UserData {
    private static final String TAG = UserData.class.getSimpleName();

    private static final String SELFIE_SUFFIX = "_selfie";
    private static final String IDCARD_SUFFIX = "_idCard";
    private static Context context = ForgetMyPictureApp.getAppContext();
    private static final String DEVICE_ID = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

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

    public Bitmap getSelfie() {
        return selfie.getValue();
    }

    public String getName() {
        return name.getValue();
    }

    public Bitmap getIdCard() {
        return idCard.getValue();
    }

    public String getEmail() {
        return email.getValue();
    }

    public String getForename() {
        return forename.getValue();
    }

    private final UserProperty<Bitmap> idCard = new UserProperty<>("idCard", DEVICE_ID + IDCARD_SUFFIX);
    private final UserProperty<Bitmap> selfie = new UserProperty<>("selfie", DEVICE_ID + SELFIE_SUFFIX);
    private final UserProperty<String> name = new UserProperty<>("name", DEVICE_ID);
    private final UserProperty<String> forename = new UserProperty<>("forename", DEVICE_ID);
    private final UserProperty<String> email = new UserProperty<>("email", DEVICE_ID);

    private final Map<String, UserProperty<?>> properties = new HashMap<>();


    private UserData() {
        properties.put(idCard.getName(), idCard);
        properties.put(selfie.getName(), selfie);
        properties.put(name.getName(), name);
        properties.put(forename.getName(), forename);
        properties.put(email.getName(), email);


        loadFromFile();
    }

    public boolean isSet() {
        return !userIsNotSet;
    }

    public void setupUserData(Bitmap idCard, Bitmap selfie, String name, String forename, String email) {
        if(isSet())
            throw new RuntimeException("User data is already set");

        this.idCard.setValue(idCard);
        this.selfie.setValue(selfie);
        this.name.setValue(name);
        this.forename.setValue(forename);
        this.email.setValue(email);

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
            dataFile = context.openFileInput(DEVICE_ID);
        } catch (FileNotFoundException e) {
            userIsNotSet = true;
            return;
        }

        try {
            JsonReader reader = new JsonReader(new FileReader(dataFile.getFD()));
            reader.beginObject();
            while (reader.hasNext()) {
                ((UserProperty<String>) properties.get(reader.nextName())).setValue(reader.nextString());
            }
            reader.endObject();
            reader.close();

            dataFile = context.openFileInput(DEVICE_ID + IDCARD_SUFFIX);
            idCard.setValue(BitmapFactory.decodeStream(dataFile));
            dataFile.close();

            dataFile = context.openFileInput(DEVICE_ID + SELFIE_SUFFIX);
            selfie.setValue(BitmapFactory.decodeStream(dataFile));
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


    private void storeToFile() throws IOException{

        FileOutputStream dataFile;
        try {
            dataFile = context.openFileOutput(DEVICE_ID, Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new FileWriter(dataFile.getFD()));
            writer.beginObject();
            for(UserProperty<?> up : properties.values()) {
                if (up.getURI().equals(DEVICE_ID)) {
                    writer.name(up.getName());
                    writer.value((String) up.getValue());
                }
            }
            writer.endObject();
            writer.close();

            dataFile = context.openFileOutput(selfie.getURI(), Context.MODE_PRIVATE);
            selfie.getValue().compress(Bitmap.CompressFormat.PNG, 0, dataFile);
            dataFile.close();

            dataFile = context.openFileOutput(idCard.getURI(), Context.MODE_PRIVATE);
            idCard.getValue().compress(Bitmap.CompressFormat.PNG, 0, dataFile);
            dataFile.close();

        } catch (IOException e) {
            wipe();
            throw e;
        }

        userIsNotSet = false;
        Log.i(TAG, "Stored data to file");
    }

    private boolean isDataValid() { //TODO: also check for non-null values validity
        return selfie.getValue() != null && idCard.getValue() != null && name.getValue() != null && forename.getValue() != null && email.getValue() != null;
    }

    public void wipe() {
        for(UserProperty<?> up : properties.values()) {
            context.deleteFile(up.getURI());
            up.setValue(null);
        }

        userIsNotSet = true;
        Log.w(TAG, "User data wiped");
    }

    public UserProperty<?> getProperty(String name) {
        return properties.get(name);
    }

    public static String getDeviceId() {
        return DEVICE_ID;
    }

    public static InputStream openFile(String URI) throws FileNotFoundException {
        return context.openFileInput(URI);
    }

    public static class UserProperty<T> {
        private String URI;
        private String name;
        private T value;

        private UserProperty(String name, String URI) {
            this.name = name;
            this.URI = URI;
            this.value = null;
        }

        private void setValue(T value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public T getValue() {
            return value;
        }

        public String getURI() {
            return URI;
        }

    }


}
