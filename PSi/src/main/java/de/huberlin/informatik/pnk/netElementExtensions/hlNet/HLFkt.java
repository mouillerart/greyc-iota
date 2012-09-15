package de.huberlin.informatik.pnk.netElementExtensions.hlNet;

public class HLFkt {
    public static String l(String x) {
        return "g" + x.substring(1);
    }

    public static Integer mal2(Integer x) {
        return new Integer(2 * x.intValue());
    }

    public static Integer plus(Integer x, Integer y) {
        return new Integer(x.intValue() + y.intValue());
    }

    public static String r(String x) {
        int fork_num = Integer.parseInt(x.substring(1)) + 1;
        if (fork_num == 4)
            fork_num = 1;
        return "g" + Integer.toString(fork_num);
    }
}