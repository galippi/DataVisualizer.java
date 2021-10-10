package dataVisualizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import utils.FileNameExtension;
import utils.dbg;

public class DataVisualizerLayoutFileLoader {
    Status status = Status.Loading;
    static final String fileNameExtension = "dvl";
    public DataVisualizerLayoutFileLoader(String baseFileName)
    {
        dbg.dprintf(9, "DataVisualizerLayoutFileLoader - open layout file for file %s!\n", baseFileName);
        status = Status.Loading;
        String layoutFileName = FileNameExtension.set(baseFileName, fileNameExtension);
        dbg.dprintf(9, "DataVisualizerLayoutFileLoader - layout file %s!\n", layoutFileName);
        InputStream is;
        try {
            is = new FileInputStream(layoutFileName);
        } catch (FileNotFoundException e) {
            dbg.dprintf(1, "Exception DataVisualizerLayoutFileLoader - unable to open file %s!\n", layoutFileName);
            status = Status.LoadingError;
            return;
        }
        JSONTokener tokener = new JSONTokener(is);
        JSONObject object = new JSONObject(tokener);
    }
    public enum Status
    {
        Loading,
        LoadingOk,
        LoadingError
    }
    public Status getStatus()
    {
        return Status.LoadingError;
    }
    public static void saveLayoutFile(String filename, DataChannelList dcl)
    {
        JSONObject json = new JSONObject();
        json.put("numOfWindows", 1);
        JSONArray windows = new JSONArray();
        JSONArray channels = new JSONArray();
        windows.put(channels);
        for (int i = 0; i < dcl.size(); i++)
        {
            JSONObject channel = new JSONObject();
            DataChannelListItem dcli = dcl.get(i);
            channel.put("name", dcli.getSignalName());
            channel.put("offset", 0.0);
            channel.put("factor", 1.0);
            channel.put("color", dcli.color.getRGB());
            channel.put("group", dcli.group.name);
            channels.put(channel);
        }
        json.put("windows", windows);
        json.put("horizontalAxle", dcl.horizontalAxle.getName());
        try {
            FileWriter myWriter = new FileWriter(filename);
            myWriter.write(json.toString());
            myWriter.close();
            dbg.dprintf(9, "DataVisualizerLayoutFileLoader.saveLayoutFile(%s) done!\n", filename);
        } catch (Exception e) {
            dbg.dprintf(1, "Exception DataVisualizerLayoutFileLoader.saveLayoutFile(%s) e=%s!\n", filename, e.toString());
        }
    }
}
