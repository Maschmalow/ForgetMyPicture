package net.tenwame.forgetmypicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings.Secure;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Created by Antoine on 20/02/2016.
 * class used as a gateway to the user personal data
 * user data is only useful while the UI is shown, so values can be cached into memory
 */
public class UserData {
    private static final String TAG = UserData.class.getSimpleName();

    private static final String SELFIE_SUFFIX = "_selfie";
    private static final String IDCARD_SUFFIX = "_idCard";
    private static final Context context = ForgetMyPictureApp.getAppContext();
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



    public Bitmap getSelfie() {
        return getPropertyValue("selfie", Bitmap.class);
    }

    public String getName() {
        return getPropertyValue("name", String.class);
    }

    public Bitmap getIdCard() {
        return getPropertyValue("idCard", Bitmap.class);
    }

    public String getEmail() {
        return getPropertyValue("email", String.class);
    }

    public String getForename() {
        return getPropertyValue("forename", String.class);
    }


    private final Map<String, UserProperty<?>> properties;
    private boolean userIsNotSet = false;


    private UserData() {
        Map<String, UserProperty<?>> props = new HashMap<>();
        addProperty(props, new UserProperty<>("idCard", DEVICE_ID + IDCARD_SUFFIX, Bitmap.class));
        addProperty(props, new UserProperty<>("selfie", DEVICE_ID + SELFIE_SUFFIX, Bitmap.class));
        addProperty(props, new UserProperty<>("name", DEVICE_ID, String.class));
        addProperty(props, new UserProperty<>("forename", DEVICE_ID, String.class));
        addProperty(props, new UserProperty<>("email", DEVICE_ID, String.class));
        properties = Collections.unmodifiableMap(props);

        loadFromFile();
    }
    private void addProperty(Map<String, UserProperty<?>> props, UserProperty<?> property) {
        props.put(property.getName(), property);
    }

    public boolean isSet() {
        return !userIsNotSet;
    }

    public void setupUserData(Bitmap idCard, Bitmap selfie, String name, String forename, String email) {
        if(isSet())
            throw new RuntimeException("User data is already set");

        setProperty("idCard", idCard);
        setProperty("selfie", selfie);
        setProperty("name", name);
        setProperty("forename", forename);
        setProperty("email", email);

        if(!isDataValid())
            return;  //isSet() == false

        try {
            storeToFile();
        } catch (IOException e) {
            throw new RuntimeException("Could not save user data");
        }

        //isSet() == true
    }

    private void checkUserSet() {
        try {
            context.openFileInput(DEVICE_ID);
        } catch (FileNotFoundException e) {
            userIsNotSet = true;
            return;
        }
        userIsNotSet = false;
    }

    private void loadFromFile() {

        checkUserSet();
        if(userIsNotSet) return;

        try {

            JsonObject stringProps = (JsonObject) Util.readJsonStructure(DEVICE_ID, context);
            for( UserProperty<?> property : getProperties() ) {
                if( property.isAssignableFrom(Bitmap.class) ) {
                    FileInputStream dataFile = context.openFileInput(property.getURI());
                    checkedCast(property, Bitmap.class).setValue(BitmapFactory.decodeStream(dataFile));
                    dataFile.close();
                }
                if(property.isAssignableFrom(String.class)) {
                    checkedCast(property, String.class).setValue(stringProps.getString(property.getName()));
                }
            }


        } catch(Exception e) {
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

        try {
            JsonObjectBuilder stringBuilder = Json.createObjectBuilder();
            for(UserProperty<?> property : getProperties()) {
                if(property.isAssignableFrom(String.class)) {
                    stringBuilder.add(property.getName(), (String) property.getValue());
                }
                if(property.isAssignableFrom(Bitmap.class)) {
                    FileOutputStream dataFile = context.openFileOutput(property.getURI(), Context.MODE_PRIVATE);
                    checkedCast(property, Bitmap.class).getValue().compress(Bitmap.CompressFormat.PNG, 0, dataFile);
                    dataFile.close();
                }
            }

            Util.writeJsonStructure(stringBuilder.build(), DEVICE_ID, context);

        } catch (IOException e) {
            wipe();
            throw e;
        }

        userIsNotSet = false;
        Log.i(TAG, "Stored data to file");
    }

    //cached values check
    private boolean isDataValid() { //TODO: also check for non-null values validity (ie. email is an address...)
        for(UserProperty<?> property :getProperties()) {
            if( property.getValue() == null )
                return false;
            UserProperty<String> stringProp = checkedCast(property, String.class);
            if( stringProp != null && stringProp.getValue().isEmpty() )
                return false;
        }
        return true;
    }

    public void wipe() {
        for(UserProperty<?> up : properties.values()) {
            context.deleteFile(up.getURI());
            up.setValue(null);
        }

        userIsNotSet = true;
        Log.w(TAG, "User data wiped");
    }

    public Collection<UserProperty<?>> getProperties() {
        return properties.values();
    }

    private <T> boolean setProperty(String name, T value) { //if the type is wrong, NPE
        if(value == null) {
            properties.get(name).setValue(null);
            return true;
        }
        checkedCast(properties.get(name), (Class<T>) value.getClass()).setValue(value);
        return true;
    }

    public UserProperty<?> getProperty(String name) {
        return properties.get(name);
    }

    public <T> T getPropertyValue(String name, Class<T> c) { //if the type is wrong, NPE
        return checkedCast(properties.get(name), c).getValue();
    }

    @SuppressWarnings("unchecked") //checks for type compatibility, and cast if successful, return null otherwise
    private static <T> UserProperty<T> checkedCast(UserProperty<?> property, Class<T> c) {
        if(property == null || !property.isAssignableFrom(c)) return null;
        return (UserProperty<T>) property;
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
        private Class<T> type;

        private UserProperty(String name, String URI, Class<T> type) {
            this.name = name;
            this.URI = URI;
            this.value = null;
            this.type = type;
        }

        private void setValue(T value) {
            if(value != null && !isAssignableFrom(value.getClass())) return;
            this.value = value;
        }

        public boolean isAssignableFrom(Class<?> c) {
            return type.isAssignableFrom(c);
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
