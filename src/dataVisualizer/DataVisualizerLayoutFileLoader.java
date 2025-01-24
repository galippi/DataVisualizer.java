package dataVisualizer;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import dataCache.DataCache_ChannelBase;
import dataCache.DataCache_FileBase;
import lippiWare.utils.FileNameExtension;
import lippiWare.utils.dbg;
import measData.dbc.DbcFile;

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
            JSONTokener tokener = new JSONTokener(is);
            jsonObject = new JSONObject(tokener);
        } catch (Exception e) {
            dbg.dprintf(1, "Exception DataVisualizerLayoutFileLoader - unable to open or load file %s!\n", layoutFileName);
            status = Status.LoadingError;
            return;
        }
        try {
            cursorsMoveTogether = jsonObject.getBoolean("CursorsMoveTogether");
        } catch (Exception e) {
            dbg.dprintf(2, "Exception DataVisualizerLayoutFileLoader - unable to get CursorsMoveTogether (%s)!\n", layoutFileName);
        }
        status = Status.LoadingOk;
    }

    public DataVisualizerLayoutFileLoader(DataCache_FileBase file) {
        jsonObject = new JSONObject();
        JSONArray windows = new JSONArray();
        jsonObject.put("windows", windows);
        JSONObject window = new JSONObject();
        windows.put(window);
        JSONArray channels = new JSONArray();
        window.put("channels", channels);
        JSONObject channel = new JSONObject();
        channels.put(channel);
        DataCache_ChannelBase ch = file.getChannel(1);
        channel.put("name", ch.getName());
        channel.put("offset", 0.0);
        channel.put("factor", 1.0);
        channel.put("color", Color.WHITE.getRGB());
        channel.put("group", ch.getName());
        ch = file.getChannel(0);
        window.put("horizontalAxle", ch.getName());
        window.put("pointIndexMin", 0);
        try {
            window.put("pointIndexMax", file.getLength() - 1);
        } catch (Exception e) {
            dbg.println(1, "DataVisualizerLayoutFileLoader.ctor file.getLength() exception e=" + e.toString());
        }
        jsonObject.put("CursorsMoveTogether", cursorsMoveTogether);
    }

    public int size()
    {
        return jsonObject.getJSONArray("windows").length();
    }

    public DataChannelList getDataChannelList(int windowIdx, DataCache_FileBase dcf) throws Exception
    {
        JSONObject window = (JSONObject)jsonObject.getJSONArray("windows").get(windowIdx);
        JSONArray channels = (JSONArray)window.getJSONArray("channels");
        Vector<DataChannelListItem> dcl = new Vector<DataChannelListItem>();
        for (int i = 0; i < channels.length(); i++)
        {
            JSONObject channel = channels.getJSONObject(i);
            String chName = channel.getString("name");
            double factor = channel.getDouble("factor");
            double offset = channel.getDouble("offset");
            Color color = new Color(channel.getInt("color"));
            String groupName = channel.getString("group");
            DataChannelListItem dcli = new DataChannelListItem(dcf, chName, factor, offset, color, groupName);
            dcl.add(dcli);
        }
        String horizontalAxleChannelName = window.getString("horizontalAxle");
        double piMin = window.getDouble("pointIndexMin");
        double piMax = window.getDouble("pointIndexMax");
        return new DataChannelList(dcf, dcl, horizontalAxleChannelName, piMin, piMax);
    }

    public void setDbc(TreeMap<Integer, Vector<String>> map, int signalMode) {
        //JSONArray channels = new JSONArray();
        JSONObject channels = new JSONObject();
        for (Map.Entry<Integer, Vector<String>>
                 entry : map.entrySet())
        {
            JSONObject o = new JSONObject();
            int chIdx = entry.getKey().intValue();
            o.put("ChIdx", chIdx);
            Vector<String> dbcs = entry.getValue();
            JSONArray jsonDbcs = new JSONArray();
            o.put("dbcs", jsonDbcs);
            for (int i = 0; i < dbcs.size(); i++) {
                JSONObject oDbc = new JSONObject();
                oDbc.put("name", dbcs.get(i));
                jsonDbcs.put(i, oDbc);
            }
            String chName = "" + chIdx;
            channels.put(chName, o);
        }
        jsonObject.put("CanChannels", channels);
        jsonObject.put("CanChannelsSignalMode", signalMode);
    }

    public String getDbcName(int chIdx, int fileIdx)
    {
        try {
            JSONObject channels = jsonObject.getJSONObject("CanChannels");
            JSONObject channel = channels.getJSONObject("" + chIdx);
            JSONArray dbcs = channel.getJSONArray("dbcs");
            JSONObject dbc = dbcs.getJSONObject(fileIdx);
            return dbc.getString("name");
        }catch (Exception e)
        {
            return null;
        }
    }

    public int getDbcSignalMode() {
        try {
            return jsonObject.getInt("CanChannelsSignalMode");
        }catch (Exception e)
        {
            return 0;
        }
    }

    public TreeMap<Integer, Vector<String>> getDbcNames() {
        TreeMap<Integer, Vector<String>> result = new TreeMap<>();
        try {
            JSONObject channels = jsonObject.getJSONObject("CanChannels");
            //Iterator<String> channelIndexes = channels.keys();
            channels.keys().forEachRemaining(key -> {
                Vector<String> dbcArray = new Vector<String>();
                JSONObject channel = channels.getJSONObject(key);
                JSONArray dbcs = channel.getJSONArray("dbcs");
                for(int fileIdx = 0; fileIdx < dbcs.length(); fileIdx++) {
                    JSONObject dbc = dbcs.getJSONObject(fileIdx);
                    dbcArray.add(dbc.getString("name"));
                }
                result.put(Integer.parseInt(key), dbcArray);
            });
            return result;
        }catch (Exception e)
        {
            return null;
        }
    }

    public String getDbcName_(int chIdx, int fileIdx)
    {
        try {
            JSONArray channels = jsonObject.getJSONArray("CanChannels");
            JSONArray channel = channels.getJSONArray(chIdx);
            JSONObject dbc = channel.getJSONObject(fileIdx);
            return dbc.getString("name");
        }catch (Exception e)
        {
            return null;
        }
    }

    public DbcFile getDbcFile(int chIdx, int fileIdx) {
        JSONObject dbc = null;
        try {
            JSONObject channels = jsonObject.getJSONObject("CanChannels");
            JSONObject channel = channels.getJSONObject("" + chIdx);
            JSONArray dbcs = channel.getJSONArray("dbcs");
            dbc = dbcs.getJSONObject(fileIdx);
            try {
                //DbcFile dbcFile = (DbcFile)dbc.get("name");
                String dbcName = (String)dbc.get("name");
                DbcFile dbcFile = new DbcFile(dbcName);
                return dbcFile;
            }catch (Exception e) {
                dbg.println(3, "DataVisualizerLayoutFileLoader.getDbcFile(" + chIdx + ", " + fileIdx + ") exception=" + e.toString());
            }
            try {
                String dbcError = dbc.getString("dbcError");
                dbg.println(11, "DBC file error (" + chIdx + ", " + fileIdx + "): " + dbcError);
                return null;
            }catch (Exception e) {
            }
            String dbcName = dbc.getString("name");
            try {
                DbcFile dbcFile = new DbcFile(dbcName);
                dbc.put("dbc", dbcFile);
                return dbcFile;
            }catch (Exception e) {
                dbc.put("dbcError", e.toString());
                return null;
            }
        }catch (Exception e)
        {
            dbg.println(2, "DBC file error (" + chIdx + ", " + fileIdx + "): " + e.toString());
            if (dbc != null)
                dbc.put("dbcError", new JSONObject(e.toString()));
            return null;
        }
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

    public void saveLayoutFile(String filename) {
        try {
            filename = FileNameExtension.set(filename, fileNameExtension);
            FileWriter myWriter = new FileWriter(filename);
            myWriter.write(jsonObject.toString());
            myWriter.close();
            dbg.dprintf(9, "DataVisualizerLayoutFileLoader.saveLayoutFile(%s) done!\n", filename);
        } catch (Exception e) {
            dbg.dprintf(1, "Exception DataVisualizerLayoutFileLoader.saveLayoutFile(%s) e=%s!\n", filename, e.toString());
        }
    }

    public static void saveLayoutFile(String filename, DataChannelList dcl)
    {
        JSONObject json = new JSONObject();
        JSONArray windows = new JSONArray();
        JSONObject window = new JSONObject();
        JSONArray channels = new JSONArray();
        window.put("channels", channels);
        for (int i = 0; i < dcl.size(); i++)
        {
            JSONObject channel = new JSONObject();
            DataChannelListItem dcli = dcl.get(i);
            channel.put("name", dcli.getSignalName());
            channel.put("offset", 0.0);
            channel.put("factor", 1.0);
            channel.put("color", dcli.color.getRGB());
            channel.put("group", dcli.group);
            channels.put(channel);
        }
        window.put("horizontalAxle", dcl.horizontalAxle.getName());
        windows.put(window);
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

    public static void saveLayoutFile(String filename, Vector<DataChannelListProvider> dataPanels, boolean cursorsMoveTogether, DataVisualizerLayoutFileLoader dvlfAdditional)
    {
        JSONObject json = new JSONObject();
        //json.put("numOfWindows", dataPanels.size());
        JSONArray windows = new JSONArray();
        for(DataChannelListProvider dataPanel: dataPanels)
        {
            DataChannelList dcl = dataPanel.getDataChannelList();
            JSONObject window = new JSONObject();
            JSONArray channels = new JSONArray();
            window.put("channels", channels);
            windows.put(window);
            for (int i = 0; i < dcl.size(); i++)
            {
                JSONObject channel = new JSONObject();
                DataChannelListItem dcli = dcl.get(i);
                channel.put("name", dcli.getSignalName());
                channel.put("offset", 0.0);
                channel.put("factor", 1.0);
                channel.put("color", dcli.color.getRGB());
                channel.put("group", dcli.group);
                channels.put(channel);
            }
            window.put("horizontalAxle", dataPanel.getDataChannelList().horizontalAxle.getName());
            window.put("pointIndexMin", dataPanel.getDataChannelList().getDataPointIndexMin());
            window.put("pointIndexMax", dataPanel.getDataChannelList().getDataPointIndexMax());
        }
        json.put("windows", windows);
        json.put("pointIndexMin", 0);
        try {
            json.put("CanChannels", dvlfAdditional.jsonObject.get("CanChannels"));
        } catch (Exception e) {
            dbg.dprintf(3, "DataVisualizerLayoutFileLoader.saveLayoutFile CanChannels exception e=%s!\n", e.toString());
        }
        try {
            json.put("CanChannelsSignalMode", dvlfAdditional.jsonObject.getInt("CanChannelsSignalMode"));
        } catch (Exception e) {
            dbg.dprintf(3, "DataVisualizerLayoutFileLoader.saveLayoutFile CanChannelsSignalMode exception e=%s!\n", e.toString());
        }
        try {
            json.put("pointIndexMax", dataPanels.get(0).getDataChannelList().file.getLength());
            json.put("CursorsMoveTogether", cursorsMoveTogether);
        } catch (Exception e) {
            dbg.dprintf(1, "DataVisualizerLayoutFileLoader.saveLayoutFile file.getLength() exception e=%s!\n", e.toString());
        }
        try {
            dbg.dprintf(9, "DataVisualizerLayoutFileLoader.saveLayoutFile(%s)\n", filename);
            filename = FileNameExtension.set(filename, fileNameExtension);
            dbg.dprintf(9, "DataVisualizerLayoutFileLoader.saveLayoutFile - layout file %s!\n", filename);
            FileWriter myWriter = new FileWriter(filename);
            myWriter.write(json.toString(2));
            myWriter.close();
            dbg.dprintf(9, "DataVisualizerLayoutFileLoader.saveLayoutFile(%s) done!\n", filename);
        } catch (Exception e) {
            dbg.dprintf(1, "Exception DataVisualizerLayoutFileLoader.saveLayoutFile(%s) e=%s!\n", filename, e.toString());
        }
    }

    public boolean cursorsMoveTogether;

    public static final int SignalModeRaw     = 0;
    public static final int SignalModeLogical = 1;
    public static final int SignalModeBoth    = 2;
}
