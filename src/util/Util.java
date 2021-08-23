package util;

public class Util
{
    public static String sprintf(String fmt, Object ... arguments)
    {
        // todo: not all c-formatter (e.g. %u) are supported
        return String.format(fmt, arguments); 
    }
}
