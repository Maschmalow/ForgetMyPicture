import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.Request;
import database.Result;
import database.User;

/**
 * Created by Antoine on 19/03/2016.
 * class that runs algorithms
 */
public class Runner {
    private static final String TAG = Runner.class.getName();
    private static final String FR_PATH = "/home/adurand00005/facial_recognition";
    private static final String IC_PATH = "/home/adurand00005/image_comparison";
    private static final String BASE_PIC_PATH = "/var/databases/";


    public void processResult(Result result) {
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
            args.addAll(user.getSelfies());
        }else {
            args.add(IC_PATH);
            args.add(result.getPicTempPath());
            args.add(request.getOriginalPicPath());
        }

        Process proc = new ProcessBuilder(args).redirectErrorStream(true).start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        result.setMatch(Integer.parseInt(reader.readLine()));
        proc.waitFor();

    }

    private static void savePic(String url, String dst) {
        try(InputStream in = new URL(url).openStream()){
            Files.copy(in, Paths.get(dst));
        } catch (Exception e) {
        }
    }
}
