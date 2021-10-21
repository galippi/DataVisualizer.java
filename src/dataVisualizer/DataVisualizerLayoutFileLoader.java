package dataVisualizer;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import dataCache.DataCache_File;
import utils.FileNameExtension;
import utils.dbg;

public class DataVisualizerLayoutFileLoader {
    Status status = Status.Loading;
    static final String fileNameExtension = "dvl";

    JSONObject jsonObject;
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
        jsonObject = new JSONObject(tokener);
        status = Status.LoadingOk;
    }

    public int size()
    {
        return jsonObject.getJSONArray("windows").length();
    }

    public DataChannelList getDataChannelList(int windowIdx, DataCache_File dcf, String horizontalAxleChannelName)
    {
        JSONArray window = (JSONArray)jsonObject.getJSONArray("windows").get(windowIdx);
        Vector<DataChannelListItem> dcl = new Vector<DataChannelListItem>();
        for (int i = 0; i < window.length(); i++)
        {
            JSONObject channel = window.getJSONObject(i);
            String chName = channel.getString("name");
            double factor = channel.getDouble("factor");
            double offset = channel.getDouble("offset");
            Color color = new Color(channel.getInt("color"));
            String groupName = channel.getString("group");
            DataChannelListItem dcli = new DataChannelListItem(chName, factor, offset, color, groupName);
            dcl.add(dcli);
        }
        return new DataChannelList(dcf, dcl, horizontalAxleChannelName);
    }

    public enum Status
    {
        Loading,
        LoadingOk,
        LoadingError
    }
    public Status getStatus()
    {
        return status;
    }
    public static void saveLayoutFile(String filename, DataChannelList dcl)
    {
        JSONObject json = new JSONObject();
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

    public static void saveLayoutFile(String filename, Vector<DataPanel> dataPanels)
    {
        JSONObject json = new JSONObject();
        json.put("numOfWindows", dataPanels.size());
        JSONArray windows = new JSONArray();
        for(DataPanel dataPanel: dataPanels)
        {
            DataChannelList dcl = dataPanel.getDataChannelList();
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
        }
        json.put("windows", windows);
        json.put("horizontalAxle", dataPanels.get(0).getDataChannelList().horizontalAxle.getName());
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
