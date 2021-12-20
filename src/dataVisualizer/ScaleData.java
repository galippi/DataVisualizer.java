package dataVisualizer;

public class ScaleData {

    ScaleData(double vMin, double vMax, int scaleMin, int scaleMax, boolean logarithmic)
    {
        if (Math.abs(vMax - vMin) < 1e-12)
        {
            this.step = 1.0;
            this.num = scaleMin;
            if (Math.abs(vMin) < 1e-12)
            {
                this.min = 0;
                this.scale = 0;
            }else
            {
                double logVal = Math.log10(Math.abs(vMin));
                int logValInt = (int)(logVal + 0.0);
                if ((logValInt >= -1) && (logValInt <= 1))
                {
                    this.min = vMin;
                    this.scale = 0;
                }else
                {
                    this.min = ((int)(vMin / Math.pow(10, logValInt - 1))) / 10.0;
                    this.scale = logValInt;
                    this.step = 0.1;
                }
            }
        }else
        {
            double delta = vMax - vMin;
            double deltaRes = delta / scaleMax;
            double logDelta = Math.log10(delta);
            double log_vMin = Math.log10(Math.abs(vMin));
            double logMax = Math.max(logDelta, log_vMin);
            int logValInt = (int)(logMax + 0.0);
            if ((logValInt >= -1) && (logValInt <= 3))
                logValInt = 0;
            this.min = ((int)(vMin / Math.pow(10, logValInt - 1))) / 10.0;
            this.scale = logValInt;
            double logDeltaRes = Math.log10(deltaRes);
            int logDeltaResInt;
            if (logDeltaRes < 0)
                logDeltaResInt = (int)(logDeltaRes - 0.999);
            else
                logDeltaResInt = (int)(logDeltaRes);
            if (logValInt > (logDeltaResInt + 2))
            {
                this.step = 0.1 * Math.pow(10, logValInt);
                this.num = scaleMin;
            }else
            {
                int deltaResScaled = ((int)((deltaRes / Math.pow(10, logDeltaResInt)) + 0.999));
                if (deltaResScaled > 5)
                    this.step = 10;
                else
                if (deltaResScaled > 2)
                    this.step = 5;
                else
                if (deltaResScaled > 1)
                    this.step = 2;
                else
                    this.step = 1;
                this.step = this.step * Math.pow(10, logDeltaResInt);
                this.num = (int)(delta / this.step + 0.99999);
            }
        }
    }

    ScaleData(double min, double step, int scale, int num)
    {
        this.min = min;
        this.step = step;
        this.scale = scale;
        this.num = num;
    }

    boolean equals(ScaleData other)
    {
        if (Math.abs(min - other.min) > 1e-12)
            return false;
        if (Math.abs(step - other.step) > 1e-12)
            return false;
        if (Math.abs(scale - other.scale) > 1e-12)
            return false;
        if (num != other.num)
            return false;
        return true;
    }

    double min;
    double step;
    int scale;
    int num;
}
