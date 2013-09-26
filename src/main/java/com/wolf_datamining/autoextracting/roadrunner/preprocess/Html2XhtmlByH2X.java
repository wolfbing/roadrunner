package com.wolf_datamining.autoextracting.roadrunner.preprocess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Html2XhtmlByH2X extends Html2XhtmlBase {

	public Html2XhtmlByH2X() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String convert(String inputPath, String outputPath) {
		// TODO Auto-generated method stub
		
		if( ! new File(inputPath).isFile())
		{
			System.out.println("error:  your input path is not a file!");
			System.exit(-1);
		}
		if( ! validName(new File(inputPath).getName()) )
		{
			return null;
		}
		
		
		String cmd[] = {"./exec/html2xhtml.exe",inputPath,"-o",outputPath,
				"--ics",this.inputEncoding,"--ocs",this.encoding};
		try {
			Runtime.getRuntime().exec(cmd);
			return outputPath;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public List<String> convertAll(String inputDir, String outputDir) {
		// TODO Auto-generated method stub
		if( ! new File(outputDir).isDirectory())
		{
			System.out.println("error:  your output dir not exist");
			System.exit(-1);
		}
		
		File f = new File(inputDir);
		List<String> outList = new ArrayList<String>();
		if(f.isDirectory())
		{
			String outDir = new File(outputDir).getAbsolutePath();
			File [] fileArr = f.listFiles();
			for(File tmpFile: fileArr)
			{
				if( tmpFile.isFile() && validName(tmpFile.getAbsolutePath()) )
				{
					String out = convert(tmpFile.getAbsolutePath(), outDir+"\\"+createXhtmlName(tmpFile.getName()) );
					if(out!=null)
					{
						outList.add(out);
					}
				}
			}
		}
		else
		{
			System.out.println("error: your input is not a directory");
		}
		
		return outList;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Html2XhtmlByH2X obj = new Html2XhtmlByH2X();
		obj.setInputEncoding("utf8");
		obj.convertAll("C:/Users/Admin/Desktop/test2/",
				"C:/Users/Admin/Desktop/result2/");
		

	}

}
