package net.tenwame.forgetmypicture.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
        Log.i(TAG, "Request for action " + curAction);
        if(!ForgetMyPictureApp.isNetworkAvailable()) {
            Log.w(TAG, "Offline mode, aborted");
            return;
        }

        try {
            handlers.get(curAction).handle(intent.getExtras());
        } catch (Exception e) {
            Log.e(TAG, "Action " + curAction + " failed", e);
            fail();
        }

        done();
    }

    final protected void fail() {
        if(listeners.get(getClass()) != null)
            for(NetworkListener listener : listeners.get(getClass()))
                listener.onActionFailed(curAction);

        curAction = null;
        stopSelf();
    }

    final protected void done() {
        if(listeners.get(getClass()) != null)
            for(NetworkListener listener : listeners.get(getClass()))
                listener.onActionFinished(curAction);

        curAction = null;
        stopSelf();
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
        void onActionFailed(String action);

        void onActionFinished(String action);
    }

    public static class EmptyListener implements NetworkListener {
        @Override
        public void onActionFailed(String action) {}
        @Override
        public void onActionFinished(String action) {}
    }
}
