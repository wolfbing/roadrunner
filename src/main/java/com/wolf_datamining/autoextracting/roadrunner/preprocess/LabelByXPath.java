package com.wolf_datamining.autoextracting.roadrunner.preprocess;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.html.dom.HTMLDocumentImpl;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;



public class LabelByXPath extends LabelBase {

	public LabelByXPath() {
		// TODO Auto-generated constructor stub
		
		
	}
	
	
	@Override
	public void label(String url, String outPath, String xpathExp)
	{
		if(!validFileName(url))
		{
			System.out.println("error: file in url has uncorrect postfix!");
			System.exit(-1);
		}
		
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		XPathExpression expr;
		try {
			expr = xpath.compile(xpathExp);
			DOMParser parser = new DOMParser();
	        parser.setProperty("http://cyberneko.org/html/properties/default-encoding",this.encoding);
	        parser.parse(url);
	        Document doc = parser.getDocument();
	        
	        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);

	        FileWriter writer = new FileWriter(outPath);
			TransformerFactory.newInstance().newTransformer().transform(
	                new DOMSource(node), new StreamResult(writer));
			writer.close();
			
			
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXNotRecognizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        
	}
	
	@Override
	public void labelAll(String inputDir, String outDir, String xpathExp)
	{
		File inputDirFile = new File(inputDir);
		File outDirFile = new File(outDir);
		if( !inputDirFile.isDirectory() || !outDirFile.isDirectory())
		{
			System.out.println("error:  please check your input-directory or output-dirctory ");
			System.exit(-1);
		}
		File[] fileList = inputDirFile.listFiles();
		for(File f: fileList)
		{
			if(f.isFile() && validFileName(f.getName()))
			{
				label(f.toURI().toString(),
						outDirFile.getAbsolutePath()+"\\"+f.getName()+"-lbl-.xhtml",xpathExp);
			}
			
		}
		
		
	}
	
	
	public static void main(String [] args)
	{
		new LabelByXPath().setInputEncoding("utf8").label(
				"file:///C:/Users/Admin/Desktop/t.html",
				"C:/Users/Admin/Desktop/labelout.xhtml", "/HTML/BODY");
//		new LabelByXPath().setInputFileEncoding("utf8").labelAll(
//				"C:/Users/Admin/Desktop/test8/",
//				"C:/Users/Admin/Desktop/test8-lbl/", "/");
		
		
		System.out.println("============  END  ==============");
	}



	

}
