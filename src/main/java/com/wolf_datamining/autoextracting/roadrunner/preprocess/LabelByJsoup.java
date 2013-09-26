package com.wolf_datamining.autoextracting.roadrunner.preprocess;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class LabelByJsoup extends LabelBase {

	public LabelByJsoup() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void label(String inputPath, String outputPath, String option)
	{
		File f = new File(inputPath);
		
//		String html = "<p>An <a href='http://example.com/'><b>example</b></a> link.</p>";
		Document doc;
		try {
			doc = Jsoup.parse(f,"utf8");
			
			Element div = doc.select(option).first();
			String linkOuterH = div.outerHtml();
			
			System.out.println(linkOuterH);
			FileWriter writer = new FileWriter(outputPath);
			writer.write(linkOuterH);
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void labelAll(String inputDir, String outputDir, String option)
	{
		File f = new File(inputDir);
		File [] fileArr  = f.listFiles();
		for(File tmpf: fileArr)
		{
			if(tmpf.isFile())
			{
				label(tmpf.getAbsolutePath(),
						new File(outputDir).getAbsolutePath()+"\\"+tmpf.getName()+"-lbl-.html",option);
			}
		}
	}
	
	
	
	public static void main(String [] args) throws IOException
	{
		
//		new LabelByJsoup().label("C:/Users/Admin/Desktop/1.htm", "C:/Users/Admin/Desktop/tmpout.htm");
		
		new LabelByJsoup().labelAll("C:/Users/Admin/Desktop/test8/",
				"C:/Users/Admin/Desktop/test8-lbl/","/");
	}
	
	

}
