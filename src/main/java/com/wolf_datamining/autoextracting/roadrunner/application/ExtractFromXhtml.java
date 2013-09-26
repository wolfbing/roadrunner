package com.wolf_datamining.autoextracting.roadrunner.application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.wolf_datamining.autoextracting.roadrunner.Shell;
import com.wolf_datamining.autoextracting.roadrunner.Wrapper;



public class ExtractFromXhtml extends AbsRoadRunnerApp {
	
	/**
	 * 无参构造函数
	 */
	public ExtractFromXhtml() {
		// TODO Auto-generated constructor stub
	
	}

	@Override
	public void generateWrapper(String fileDir, String outDir) {
		// TODO Auto-generated method stub
		
//		if(!new File(outDir).isDirectory())
//		{
//			System.out.println("error:  your outDir is not correct!");
//			System.exit(-1);
//		}
		
		File f = new File(fileDir);
		if(f.isDirectory())
		{
			String[] xhtmlList = f.list();
			String conf = "-O" + this.confPath ;
			String out = "-N" + outDir;
			
			List<String> argList = new ArrayList<String>();
			argList.add(conf);
			argList.add(out);
			
			for(String xhtml: xhtmlList)
			{
				System.out.println(xhtml);
				argList.add(fileDir+xhtml);
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

	/**
	 * @param filePath - 待抽取的文件的路径，不是url
	 * @param resultDir - 抽取结果存放的路径，不含文件名
	 */
	@Override
	public void extract(String wrapperPath,String filePath,String resultDir) {
		// TODO Auto-generated method stub
		
		List<String> argList = new ArrayList<String>();
		argList.add(wrapperPath);
		argList.add(new File(resultDir).getAbsolutePath() + "\\");
		argList.add(this.inputEncoding);
		File f = new File(filePath);
		
		argList.add(f.toURI().toString());

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
	public void extractAll(String wrapperPath,String inputDir,String resultDir) {
		// TODO Auto-generated method stub
		
		List<String> argList = new ArrayList<String>();
		argList.add(wrapperPath);
		argList.add(new File(resultDir).getAbsolutePath() + "\\");
		argList.add(this.inputEncoding);
		File f = new File(inputDir);
		File[] fileList = f.listFiles();
		for(File tmpFile: fileList)
		{
			if(tmpFile.isFile())
			{
				argList.add(tmpFile.toURI().toString());
			}
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
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ExtractFromXhtml parser = new ExtractFromXhtml();
		
		parser.setInputFileEncoding("utf8");
		
		parser.generateWrapper("C:/Users/Admin/Desktop/test11-xml/", "test11");
//		parser.extract("G:/datamining/Projects/roadrunnertest/output/test11/test1100.xml",
//				"C:/Users/Admin/Desktop/test11-xml/1.xhtml", 
//				"G:/datamining/Projects/roadrunnertest/output/test11/");
//		parser.extractAll("G:/datamining/Projects/roadrunnertest/output/tmp/tmp00.xml",
//				"C:/Users/Admin/Desktop/tmp/",
//				"G:/datamining/Projects/roadrunnertest/output/tmp/");
		System.out.println("============  END   ==============");
		


	}

	

}
