package com.wolf_datamining.autoextracting.roadrunner.application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.wolf_datamining.autoextracting.roadrunner.Shell;
import com.wolf_datamining.autoextracting.roadrunner.Wrapper;
import com.wolf_datamining.autoextracting.roadrunner.preprocess.Html2Xhtml;
import com.wolf_datamining.autoextracting.roadrunner.preprocess.Html2XhtmlByNeko;


public class ExtractFromHtml extends AbsRoadRunnerApp {
	
	Html2Xhtml converter ;

	public ExtractFromHtml() {
		// TODO Auto-generated constructor stub
		
		converter = new Html2XhtmlByNeko();
		converter.setInputEncoding(this.inputEncoding);
		converter.setOutputEncoding(this.inputEncoding);
		
	}
	
	@Override
	public void setInputFileEncoding(String encode)
	{
		this.inputEncoding = encode;
		converter.setOutputEncoding(encode);
		converter.setInputEncoding(encode);
	}
	
	
	/**
	 * 设置HTML到xhtml的转换器，默认使用nekohtml的转换器
	 * @param convert
	 */
	public void setHtmlToXhtmlTool(Html2Xhtml convert)
	{
		this.converter = convert;
	}
	
	/**
	 * outDir - 表示相对于output的路径
	 */
	@Override
	public void generateWrapper(String fileDir, String outDir) {
		// TODO Auto-generated method stub
//		if(!new File(outDir).isDirectory())
//		{
//			System.out.println("error:  your outDir is not correct!");
//			System.exit(-1);
//		}
		
		File f = new File(fileDir);
		
		String newFileDir = f.getAbsolutePath() + "\\extract\\";
		new File(newFileDir).mkdir();
		if(f.isDirectory())
		{
			List<String> fileList = converter.convertAll(fileDir, newFileDir);
			
			String conf = "-O" + this.confPath ;
			String out = "-N" + outDir;
			
			List<String> argList = new ArrayList<String>();
			argList.add(conf);
			argList.add(out);
			
			for(String xhtml: fileList)
			{
//				System.out.println(xhtml);
				argList.add(xhtml);
			}
			
			String [] argv =new String[argList.size()];
			argList.toArray(argv);
					
			try {
				new Shell().setFileEncoding(this.inputEncoding).disguiseShell(argv);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else
		{
			System.out.println("error:  your input dir is not correct!");
			System.exit(-1);
		}
		
	}

	@Override
	public void extract(String wrapperPath, String filePath, String resultDir) {
		// TODO Auto-generated method stub
		
		List<String> argList = new ArrayList<String>();
		argList.add(wrapperPath);
		argList.add(new File(resultDir).getAbsolutePath() + "\\");
		argList.add(this.inputEncoding);
		File htmlFile = new File(filePath);
		new File(htmlFile.getParent()+"\\extract\\").mkdir();
		String xhtmlPath = converter.convert(htmlFile.toURI().toString(),
				htmlFile.getParent()+"\\extract\\"+htmlFile.getName()+"-to-.xhtml");
		argList.add(new File(xhtmlPath).toURI().toString());

		String args[] = new String[argList.size()];
		argList.toArray(args);
		try {
			Wrapper.extract(args);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

	@Override
	public void extractAll(String wrapperPath, String inputDir, String resultDir) {
		// TODO Auto-generated method stub
		List<String> argList = new ArrayList<String>();
		argList.add(wrapperPath);
		argList.add(new File(resultDir).getAbsolutePath() + "\\");
		argList.add(this.inputEncoding);
		new File(new File(inputDir).getAbsoluteFile()+"\\extract\\").mkdir();
		List<String> listFile = converter.convertAll(inputDir, new File(inputDir).getAbsolutePath()+"\\extract\\");
		for(String f: listFile)
		{
			argList.add(new File(f).toURI().toString());
		}

		String args[] = new String[argList.size()];
		argList.toArray(args);
		try {
			Wrapper.extract(args);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static void main(String [] args)
	{
		ExtractFromHtml parser = new ExtractFromHtml();
		parser.setInputFileEncoding("gb2312");
		parser.generateWrapper("C:/Users/Admin/Desktop/jd-标注更小/", "newtest");
//		parser.extract("G:/datamining/Projects/roadrunnertest/output/newtest/newtest00.xml",
//				"C:/Users/Admin/Desktop/jd-标注更小/1.html",
//				"G:/datamining/Projects/roadrunnertest/output/newtest/");
		
//		parser.extractAll("G:/datamining/Projects/roadrunnertest/output/newtest/newtest00.xml",
//				"C:/Users/Admin/Desktop/jd-标注更小/", "G:/datamining/Projects/roadrunnertest/output/newtest/");
		
		
		System.out.println("============= END ===============");
	}
	
	
	
	
	

}
