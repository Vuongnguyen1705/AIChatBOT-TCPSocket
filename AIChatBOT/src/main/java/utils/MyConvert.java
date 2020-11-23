package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author HUNGVUONG
 */
public class MyConvert {

    public static String convertTime(String time) {        
        Date date = new Date(Long.parseLong(time+"000"));
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return format.format(date);
    }
    public static String convertTimeToHours(String time) {        
        Date date = new Date(Long.parseLong(time+"000"));
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(date);
    }
    public static void main(String[] args) {

        System.out.println(  convertTime("1606111881"));
    }
}
