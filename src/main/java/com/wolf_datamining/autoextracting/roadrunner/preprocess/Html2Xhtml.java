package com.wolf_datamining.autoextracting.roadrunner.preprocess;

import java.util.List;

public interface Html2Xhtml {
	
	/**
	 * 设置输出文件的编码
	 */
	public Html2Xhtml setOutputEncoding(String encoding);
	
	/**
	 * 设置输入文件的编码，提示：中文gb2312,日文shift_jis,国际utf8
	 * 
	 * @param encoding
	 * @return
	 */
	public Html2Xhtml setInputEncoding(String encoding);
	
//	public boolean validName(String name);
//	
//	public void setPostfix(List<String> postfix);
//	
//	public void addPostfix(List<String> postfix);
	
	public String  convert(String inputUrl, String outputPath);
	
	public List<String> convertAll(String inputDir, String outputDir );
	

}
