package dataVisualizer;

import dataCache.DataCache_File;
import utils.dbg;

public class DataVisualizerLayout {
    static boolean checkConsistency(DataCache_File dcf, DataVisualizerLayoutFileLoader dvlf)
    {
        for (int i = 0; i < dvlf.size(); i++)
        {
            DataChannelList dvlfi;
            try {
                dvlfi = dvlf.getDataChannelList(i, dcf);
            } catch (Exception e) {
                dbg.dprintf(1, "DataVisualizerLayout.checkConsistency i=%d e=%s!\n", i, e.toString());
                return false;
            }
            for (int j = 0; j < dvlfi.size(); j++)
            {
                if (dvlfi.get(j) == null)
                {
                    dbg.dprintf(1, "DataVisualizerLayout.checkConsistency i=%d j=%d chName=%s!\n", i, j, dvlfi.getChName(j));
                    return false;
                }
            }
        }
        return true;
    }
}
