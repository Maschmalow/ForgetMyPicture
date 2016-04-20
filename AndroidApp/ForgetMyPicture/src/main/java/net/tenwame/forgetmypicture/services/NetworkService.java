package net.tenwame.forgetmypicture.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crittercism.app.Crittercism;

import net.tenwame.forgetmypicture.ForgetMyPictureApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Antoine on 17/04/2016.
 * Service template for networking
 */
public abstract class NetworkService extends IntentService {
    private static final String TAG = NetworkService.class.getSimpleName();

    private static Map<Class<? extends NetworkService>, List<NetworkListener>> listeners = new HashMap<>();

    protected Map<String, ActionHandler> handlers = new HashMap<>();
    private String curAction;

    public NetworkService(String name) {
        super(name);
    }

    public static void execute(Class<? extends IntentService> clazz, String action) {
        execute(clazz, action, null);
    }

    public static void execute(Class<? extends IntentService> clazz, String action, Bundle params) {
        Intent intent = new Intent(ForgetMyPictureApp.getContext(), clazz);
        intent.setPackage(ForgetMyPictureApp.getName());
        intent.setAction(action);
        if(params != null)
            intent.putExtras(params);
        ForgetMyPictureApp.getContext().startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        curAction = intent.getAction();
        if(curAction == null) {
            Log.w(TAG, "No action, aborted");
            return;
        }
        Log.i(TAG, "Request for action " + curAction);
        if(!ForgetMyPictureApp.isNetworkAvailable()) {
            Log.w(TAG, "Offline mode, aborted");
            return;
        }

        try {
            handlers.get(curAction).handle(intent.getExtras());
        } catch (Exception e) {
            Log.e(TAG, "Action " + curAction + " failed", e);
            Crittercism.logHandledException(e);
            notifyFailed();
            return;
        }

        notifyFinished();
        curAction = null;
    }

    private void notifyFailed() {
        if(listeners.get(getClass()) != null)
            for(NetworkListener listener : listeners.get(getClass()))
                listener.onActionFailed(curAction);
    }

    private void notifyFinished() {
        if(listeners.get(getClass()) != null)
            for(NetworkListener listener : listeners.get(getClass()))
                listener.onActionFinished(curAction);
    }

    protected interface ActionHandler {
        void handle(Bundle params) throws Exception;
    }

    public static void registerListener(Class<? extends  NetworkService> clazz, NetworkListener listener) {
        List<NetworkListener> classListeners = listeners.get(clazz);
        if(classListeners == null) {
            classListeners = new ArrayList<>();
            listeners.put(clazz, classListeners);
        }

        classListeners.add(listener);
    }

    public static void unregisterListener(Class<? extends  NetworkService> clazz, NetworkListener listener) {
        List<NetworkListener> classListeners = listeners.get(clazz);
        if(classListeners != null) {
            classListeners.remove(listener);
        }
    }

    public interface NetworkListener {
        void onActionFailed(@NonNull  String action);

        void onActionFinished(@NonNull String action);
    }

    public static class EmptyListener implements NetworkListener {
        @Override
        public void onActionFailed(@NonNull String action) {}
        @Override
        public void onActionFinished(@NonNull String action) {}
    }

    public static class ActionListener implements  NetworkListener {
        private final String action;

        public ActionListener(String action) {
            this.action = action;
        }

        @Override
        public void onActionFinished(@NonNull String action) {
            if(action.equals(this.action))
                onFinished();
        }

        @Override
        public void onActionFailed(@NonNull String action) {
            if(action.equals(this.action))
                onFailed();
        }

        //ActionListener can't be an interface, so there's no use in making it abstract here
        public void onFinished() {}
        public void onFailed() {}
    }
}
