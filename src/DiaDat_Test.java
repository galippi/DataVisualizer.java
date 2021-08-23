import diaDat.DiaDat_ChannelBase;
import diaDat.DiaDat_File;
import util.Util;

public class DiaDat_Test
{
    static void testInvalidChannel(DiaDat_File file) throws Exception
    {
        boolean isOk = true;
        try
        {
            DiaDat_ChannelBase chTime = file.getChannel("time2");
            isOk = false;
        }catch(Exception e)
        {
            System.out.println("testInvalidChannel: Good exception e=" + e.toString());
        }
        if (!isOk)
            throw new Exception("testInvalidChannel: error - no exception!");
    }

    static void testCase(int leftVal, int rightVal) throws Exception
    {
        if (leftVal != rightVal)
            throw new Exception(Util.sprintf("testCase: error - leftVal != rightVal (%d != %d)!", leftVal, rightVal));
    }

    static void testCase(boolean val) throws Exception
    {
        if (!val)
            throw new Exception("testCase: error - it's not true!");
    }

    static void testCase(double leftVal, double rightVal, double tolerance) throws Exception
    {
        if (Math.abs(leftVal -rightVal) > tolerance)
            throw new Exception(Util.sprintf("testCase: error - leftVal != rightVal (%f != %f)!", leftVal, rightVal));
    }

    public static void main(String[] args)
    {
        String currentDirectory = System.getProperty("user.dir");
        System.out.println("The current working directory is " + currentDirectory);
        try
        {
            DiaDat_File file = new DiaDat_File("TestData/test06.DAT");
            DiaDat_ChannelBase chTime = file.getChannel("time");
            DiaDat_ChannelBase chDiagConnected = file.getChannel("Diag_Connected");
            DiaDat_ChannelBase chCpuTemp = file.getChannel("_CPUTemperature_");
            DiaDat_ChannelBase chV_veh = file.getChannel("V_Veh");
            DiaDat_ChannelBase chP_Whl1 = file.getChannel("P_Whl1");
            DiaDat_ChannelBase chAxle_load_1 = file.getChannel("Axle_load_1");
            DiaDat_ChannelBase chAxle_load_2 = file.getChannel("Axle_load_2");
            System.out.println("V_Veh0=" + chV_veh.getValueDouble());
            testCase(chTime.getValueRaw(), 0);
            testCase(chDiagConnected.getValueRaw(), 0x01);
            testCase(chCpuTemp.getValueRaw(), 51);
            testCase(chAxle_load_1.getValueDouble(),  6000.0, 0.01);
            testCase(chAxle_load_2.getValueDouble(), 10000.0, 0.01);
            file.step();
            System.out.println("V_Veh1=" + chV_veh.getValueDouble());
            testCase(chTime.getValueRaw(), 1);
            testCase(chDiagConnected.getValueRaw(), 0x01);
            testCase(chCpuTemp.getValueRaw(), 51);
            testCase(chAxle_load_1.getValueDouble(),  6000.0, 0.01);
            testCase(chAxle_load_2.getValueDouble(), 10000.0, 0.01);
            file.step();
            for (int i = 2; i < chDiagConnected.getLength(); i++)
            {
                //System.out.println("V_Veh["+i+"]=" + chV_veh.getValueDouble());
                System.out.println("time="+chTime.getValueDouble()+" P_Whl1["+i+"]=" + chP_Whl1.getValueDouble() + " V_Veh["+i+"]=" + chV_veh.getValueDouble());
                testCase(chTime.getValueRaw(), i);
                //testCase(chDiagConnected.getValueRaw(), 0x01);
                if (i < (chDiagConnected.getLength() - 1))
                    file.step();
                else
                    testCase(!file.stepIsOk());
            }
            testCase(!file.stepIsOk());
        }catch(Exception e)
        {
            System.out.println("main exception e=" + e.toString());
            System.exit(1);
        }
        System.out.println("main is done, no error.");
    }
}
