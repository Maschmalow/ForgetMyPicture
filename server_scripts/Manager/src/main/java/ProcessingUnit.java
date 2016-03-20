import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
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
public class ProcessingUnit implements Runnable {
    private static final Logger logger = Logger.getLogger(ProcessingUnit.class.getName());
    private static final String FR_PATH = "/home/adurand00005/facial_recognition";
    private static final String IC_PATH = "/home/adurand00005/image_comparison";
    private static final String BASE_PIC_PATH = "/var/databases/files";

    private Result result;

    public ProcessingUnit(Result toProcess) {
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

        String dst = BASE_PIC_PATH + user.getDeviceId() + "_" +
                request.getId() + "_" + UUID.randomUUID() +
                result.getPicURL().substring(0,result.getPicRefURL().lastIndexOf('.'));
        result.setPicTempPath(dst);
        Main.getResultDao().update(result);
        savePic(result.getPicURL(), dst);

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

        try {
            logger.log(Level.INFO, "Starting process...");
            Process proc = new ProcessBuilder(args).redirectErrorStream(true).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            result.setMatch(Integer.parseInt(reader.readLine()));
            Main.getResultDao().update(result);
            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private static void savePic(String url, String dst) {
        try(InputStream in = new URL(url).openStream()){
            Files.copy(in, Paths.get(dst));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}