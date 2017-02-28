package nio;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by migle on 2016/11/18.
 */
public class Time1 {
    public static void main(String[] args) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd HHmmssSS");
        Date d = new Date();
        //Instant timestamp = Instant.now();
        //System.out.println(sf.format(d));
        Instant timestamp =  Instant.ofEpochMilli(System.currentTimeMillis());
        System.out.println(timestamp.toString());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        LocalDateTime ldt = LocalDateTime.ofInstant(timestamp, ZoneId.of("Asia/Shanghai"));
        System.out.println(ZoneId.systemDefault());

        System.out.println(ldt.format(formatter));

    }
}
