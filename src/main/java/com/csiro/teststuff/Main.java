package com.csiro.teststuff;

import static com.csiro.teststuff.App.ConcatANUemail;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.poi.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Main {

    public static void main(String[] args) throws Exception {

////        ConcatANUemail();
//        AdmissionController a = new AdmissionController();
//        for (int i = 1; i <= 33; i++) {
//            //System.out.println(a.SinglePointAdmission(i, 25));
//            System.out.println(a.DistributionBasedAdmission(i, 85));
////            System.out.println("============="+i+"===============");
//           // System.out.println(a.missSLA("C:\\Users\\kho01f\\Desktop\\First Contribution\\TPC-H\\AdmissionControl\\mix"+i+".csv", 95, 1));
//        double [] p1 = a.CalcPdfProb(24, 70, 33);
//        System.out.println(p1[0]+ "...."+p1[1]);
//        double [] p2 = a.CalcPdfProb(37, 87, 33);
//        System.out.println(p2[0]+ "...."+p2[1]);
        ConcatANUemail();
        //}

    }

    public static Object execute(String date_str) throws ParseException {
        Date date = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(date_str);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        return new Integer(year);
    }

    public static Object execute2(String str) {
        return new String(str.substring(0, 2));
    }

    public static void ConcatANUemail() {

        try {
            String line = "";
            String newStr = "";
            File file = new File("C:/Users/kho01f/Desktop/DB Lab 2014/2015/std_uid.csv");
            BufferedReader in = new BufferedReader(new FileReader(file));
            while ((line = in.readLine()) != null) {
                String[] Id = line.split(" ");
                for (int i = 0; i < Id.length; i++) {
                    Id[i] = Id[i].concat("@anu.edu.au;");
                    newStr = newStr + Id[i] + "\r\n";
                }
            }
            FileWriter writer = new FileWriter("C:/Users/kho01f/Desktop/DB Lab 2014/2015/std_uid.csv");
            writer.write(newStr);

            writer.close();
            in.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
