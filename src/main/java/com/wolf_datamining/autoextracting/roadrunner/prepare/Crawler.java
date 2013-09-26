package com.wolf_datamining.autoextracting.roadrunner.prepare;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Crawler {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			Document doc = Jsoup.connect("http://www.douban.com/event/19500282/").get();
			
//			System.out.println(doc.html());
			System.out.println(doc.title());
//			System.out.println(new URL("http://wwww.baidu.com/1.html").getFile());

			OutputStream outstream = new FileOutputStream("C:/Users/Admin/Desktop/douban/"
			+doc.title().replace("/", "-").replace("\\", "-").replace(":", "-").replace(" ", "")
			.replace("\n", "-")+".html");
			OutputStreamWriter writer = new OutputStreamWriter(outstream,"utf8");
			writer.write(doc.html());
			writer.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

}
