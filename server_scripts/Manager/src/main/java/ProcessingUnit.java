import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import database.Request;
import database.Result;
import database.Selfie;
import database.User;

/**
 * Created by Antoine on 19/03/2016.
 * class that runs algorithms
 */
class ProcessingUnit implements Runnable {
    private static final Logger logger = Logger.getLogger(ProcessingUnit.class.getName());
    private static final String FR_PATH = System.getenv("MANAGER_PATH") + "FR/Recognize";
    private static final String IC_PATH = System.getenv("MANAGER_PATH") + "IC/recognize";
    private static final String BASE_FILES_PATH = System.getenv("BASE_FILES_PATH");

    private final Result result;

    ProcessingUnit(Result toProcess) {
        result = toProcess;
    }

    @Override
    public void run() {
        if(result == null) return;

        try {
            processResult();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL error while processing result", e);
            throw new RuntimeException(e);
        }
    }

    private void processResult() throws SQLException {
        Main.getResultDao().refresh(result);
        Request request = result.getRequest();
        Main.getRequestDao().refresh(request);
        User user = request.getUser();
        Main.getUserDao().refresh(user);

        String dst = BASE_FILES_PATH + user.getDeviceId() + "_" +
                request.getId() + "_" + UUID.randomUUID() +
                result.getPicURL().substring(0,result.getPicURL().lastIndexOf('.'));
        savePic(result.getPicURL(), dst);
        result.setPicTempPath(convertPic(dst));
        Main.getResultDao().update(result);

        List<String> args = new ArrayList<>();
        if(request.getKind() == Request.Kind.EXHAUSTIVE) {
            args.add(FR_PATH);
            args.add(result.getPicTempPath());
            for( Selfie selfie : user.getSelfies() ) {
                Main.getSelfieDao().refresh(selfie);
                args.add(selfie.getPath());
            }
        }else {
            args.add(IC_PATH);
            args.add(result.getPicTempPath());
            args.add(request.getOriginalPicPath());
        }
        args.add(BASE_FILES_PATH + "error_log_" + new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss.SS").format(Calendar.getInstance().getTime()));

        try {
            logger.log(Level.INFO, "Starting process...");
            Process proc = new ProcessBuilder(args).redirectErrorStream(true).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            result.setMatch(Integer.parseInt(reader.readLine()));
            Main.getResultDao().update(result);
            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, "Could process result " + result.getPicURL());
            throw new RuntimeException(e);
        }

    }

    private static void savePic(String url, String dst) {
        try(InputStream in = new URL(url).openStream()){
            Files.copy(in, Paths.get(dst));
        } catch (Exception e) {
            throw new RuntimeException("Could not save picture from " + url, e);
        }
    }

    private static String convertPic(String path) {
        String convertedPath = path.substring(0, path.lastIndexOf('.')) + "ppm";
        try {
            new ConvertCmd().run(new IMOperation().addImage().addImage(), path, convertedPath);
            Files.delete(Paths.get(path));
        } catch (IOException | IM4JavaException | InterruptedException e) {
            throw new RuntimeException("Could not convert image", e);
        }
        return convertedPath;
    }

}
