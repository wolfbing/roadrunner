package com.wolf_datamining.autoextracting.roadrunner.preprocess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.cyberneko.html.filters.Purifier;
import org.cyberneko.html.filters.Writer;
import org.cyberneko.html.parsers.DOMParser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * 
 * 使用nekohtml进行转换
 * 
 * @author Admin
 *
 */
public class Html2XhtmlByNeko extends Html2XhtmlBase {
	
	public Html2XhtmlByNeko()
	{
		
	}

	/**
	 * 单文件转换
	 * @param inputUrl - String, 待转换文件的URL
	 * @param outputPath - String, 输出文件的路径
	 * 
	 * @return - 如果成功转换，则返回xhtml文件名，如果转换失败，则返回null
	 * 
	 */
	@Override
	public String convert(String inputUrl, String outputPath) {
		
		// TODO Auto-generated method stub
		// http://www.iana.org/assignments/character-sets/character-sets.xhtml
		//  编码设置的词要用上面地址里的标准
		OutputStream fileOut;
		try {
			if(!validName(inputUrl))
			{
				return null;
			}
			
			fileOut = new FileOutputStream(outputPath);
			XMLDocumentFilter writer = new Writer(fileOut,this.encoding);
			Purifier purifier = new Purifier();
			XMLDocumentFilter [] filters = {writer,purifier};
			
			DOMParser parser = new DOMParser();
			parser.setProperty("http://cyberneko.org/html/properties/default-encoding", this.inputEncoding);
			parser.setProperty("http://cyberneko.org/html/properties/filters", 
					filters);
			parser.parse(inputUrl);
			
			return outputPath;
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXNotRecognizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
		
	}

	
	/**
	 * 进行批量转换，用于本地已知文件路径的转换
	 * 
	 * @param inputDir - 输入文件的路径，到文件夹
	 * @param outputDir - 输出文件的路径，到输出文件夹
	 * 
	 * @return - 返回生成的xhtml文件的列表
	 * 
	 */
	@Override
	public List<String> convertAll(String inputDir, String outputDir) {
		// TODO Auto-generated method stub
		
		if(! new File(outputDir).isDirectory())
		{
			System.out.println("output dir not exist");
			System.exit(-1);
		}
		
		File f = new File(inputDir);
		List<String> xhtmls = new ArrayList<String>();
		if(f.isDirectory())
		{
//			String [] fileArr  =f.list();
			String outDir = new File(outputDir).getAbsolutePath();
			File [] files = f.listFiles();
			for(File tmpFile: files)
			{
				if(tmpFile.isFile() && validName(tmpFile.getAbsolutePath()))
				{
					String out = convert(tmpFile.toURI().toString(),
							outDir+"\\"+createXhtmlName(tmpFile.getName()) );
					if(out!=null)
					{
						xhtmls.add(out);
					}
				}
			}
		}
		else
		{
			System.out.println("error:  your input is not a dirctory!!");
			System.exit(-1);
		}
		
		return xhtmls;
		
		
	}

	
	
	


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Html2XhtmlByNeko obj = new Html2XhtmlByNeko();
		obj.setOutputEncoding("utf8"); // 输出编码的设置，如果不设置将采用输入编码
		obj.setInputEncoding("gbk"); // 如果HTML没有声明编码，这个一定要设置，否则不需要设置
//		obj.convert("file:///C:/Users/Admin/Desktop/%E6%A0%87%E6%B3%A8%E5%90%8E%E7%9A%84%E7%9B%B8%E4%BC%BC%E9%A1%B5%E9%9D%A2/1.html", 
//				"C:/Users/Admin/Desktop/11.xhtml");
		obj.convertAll("C:/Users/Admin/Desktop/相似页面/",
				"C:/Users/Admin/Desktop/test11-xml/");

	}
	


}
