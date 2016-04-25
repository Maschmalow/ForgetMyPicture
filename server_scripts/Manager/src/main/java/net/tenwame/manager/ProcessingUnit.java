package net.tenwame.manager;

import net.tenwame.manager.database.Request;
import net.tenwame.manager.database.Result;
import net.tenwame.manager.database.Selfie;
import net.tenwame.manager.database.User;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.Operation;

import java.io.BufferedReader;
import java.io.File;
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

/**
 * Created by Antoine on 19/03/2016.
 * class that runs algorithms
 */
class ProcessingUnit implements Runnable {
    private static final Logger logger = Logger.getLogger(ProcessingUnit.class.getName());
    private static final String FR_PATH = System.getenv("MANAGER_PATH") + "FR/Recognizer";
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
        User user = request.getUser();


        String dst = BASE_FILES_PATH + user.getDeviceId() + "_" +
                request.getId() + "_" + UUID.randomUUID();
        savePic(result.getPicURL(), dst);
        String picTmpPath = convertPic(dst, true);

        List<String> args = new ArrayList<>();
        String originalPicConverted = null;
        if(request.getKind() == Request.Kind.EXHAUSTIVE) {
            args.add(FR_PATH);
            args.add(picTmpPath);
            for( Selfie selfie : user.getSelfies() ) {
                args.add(selfie.getPath());
            }
        }
        if(request.getKind() == Request.Kind.QUICK) {
            args.add(IC_PATH);
            args.add(picTmpPath);
            originalPicConverted = convertPic(request.getOriginalPicPath(), false);
            args.add(originalPicConverted);
        }
        args.add(BASE_FILES_PATH + "error_log_" + new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss.SS").format(Calendar.getInstance().getTime()));
        //File err = new File(BASE_FILES_PATH + "error_log_" + new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss.SS").format(Calendar.getInstance().getTime()));

        try {
            logger.log(Level.INFO, "Starting process" + args.toString() );
            Process proc = new ProcessBuilder(args).directory(new File(new File(FR_PATH).getParent())).redirectErrorStream(true).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            proc.waitFor();
            String ret = reader.readLine();
            logger.log(Level.INFO, result.getPicURL() + " result: " + ret);
            result.setMatch(Integer.parseInt(ret));
            Main.getResultDao().update(result);

        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, "Could not process result " + result.getPicURL(), e);
            throw new RuntimeException(e);
        } finally {
            try {
                if(originalPicConverted != null)
                    Files.delete(Paths.get(originalPicConverted));
                Files.delete(Paths.get(picTmpPath));
            } catch (IOException e) {
                logger.log(Level.WARNING, "Could not delete converted file", e);
            }
        }
    }

    private static void savePic(String url, String dst) {
        try(InputStream in = new URL(url).openStream()){
            Files.copy(in, Paths.get(dst));
        } catch (Exception e) {
            throw new RuntimeException("Could not save picture from " + url, e);
        }
    }

    private static final Operation convertOp = new IMOperation().addImage().addImage();
    private static String convertPic(String path, boolean delete) {
        //I know, that's a lot of UUID's, but it's a cheap and efficient solution to filename collisions
        String convertedPath = path + UUID.randomUUID() + ".ppm";
        try {
            new ConvertCmd().run(convertOp, path, convertedPath);
        } catch (IOException | IM4JavaException | InterruptedException e) {
            throw new RuntimeException("Could not convert image", e);
        } finally {
            if(delete) {
                try {
                    Files.delete(Paths.get(path));
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Could not delete source file " + path, e);
                }
            }
        }

        return convertedPath;
    }

}
