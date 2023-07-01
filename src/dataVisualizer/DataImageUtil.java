package dataVisualizer;

import dataCache.DataCache_ChannelBase;
import lippiWare.utils.dbg;

class HPosData {
    public double tMin, tMax;
    public int tIdxLowInt, tIdxHighInt;
}

public class DataImageUtil {
    public static HPosData calcBorders(final DataChannelList dcl) {
        try {
            final DataCache_ChannelBase chHor = dcl.getHorizontalAxle();
            HPosData hPosData = new HPosData();
            int tIdxMax;
            tIdxMax = dcl.file.getLength() - 1;
            double tIdxLow = dcl.getDataPointIndexMin();
            double tIdxHigh = dcl.getDataPointIndexMax();
            hPosData.tIdxLowInt = Math.max((int)(tIdxLow + 0.5), 0);
            hPosData.tMin = chHor.getDouble(hPosData.tIdxLowInt);
            hPosData.tIdxHighInt = Math.min((int)(tIdxHigh + 0.5), tIdxMax);
            hPosData.tMax = chHor.getDouble(hPosData.tIdxHighInt);
            double dt = hPosData.tMax - hPosData.tMin;
            int dIdxHighInt = hPosData.tIdxHighInt - hPosData.tIdxLowInt;
            double factor = dt / dIdxHighInt;
            hPosData.tMin = hPosData.tMin - (hPosData.tIdxLowInt - tIdxLow) * factor;
            hPosData.tMax = hPosData.tMax + (tIdxHigh - hPosData.tIdxHighInt) * factor;
            return hPosData;
        } catch (Exception e) {
            //e.printStackTrace();
            dbg.println(1, "DataImageUtil.calcBorders exception e=" + e.toString());
            return null;
        }
    }

}
