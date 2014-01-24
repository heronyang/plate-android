package tw.plate;

/**
 * Created by aje on 1/24/14.
 */
public class Tools {
    public String formattedNS(int ns) {
        String res = "";
        ns %= 26 * 10 * 10;
        res += Character.toString ((char) ((ns/100) + 'A'));
        res += Integer.toString(ns%100);
        return res;
    }
}
