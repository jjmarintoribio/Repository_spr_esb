package ib.visa.esb.bth.gen;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
String nomNewFile ="ARCHIVO.CSV.part";

SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMddhhmmssSSS");
System.out.println(nomNewFile.substring(0,nomNewFile.length()-9)+ ".dat");
int x = 1460419358;
long d = Long.parseLong("1460419358");
System.out.println((new Date(d)));


	}

}
