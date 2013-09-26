package com.wolf_datamining.autoextracting.roadrunner.application;

/**
 * 抽象类，作为ExtractFrom(X)Html的基类
 * 
 * @author Wolf 2013-7-31
 *
 */
public abstract class AbsRoadRunnerApp implements RoadRunnerApp {
	
	/**
	 * 输入文件的编码
	 */
	protected String inputEncoding;
	
	/**
	 * 配置文件的路径
	 */
	protected String confPath;
	

	public AbsRoadRunnerApp() {
		// TODO Auto-generated constructor stub
		
		inputEncoding = "utf8";
		confPath = "./etc/wrapperconf.xml";
		
	}
	
	/**
	 * 设置输入文件的编码
	 * 
	 * @param encode - 文件编码
	 */
	public void  setInputFileEncoding(String encode)
	{
		inputEncoding = encode;
	}
	
	/**
	 * 设置配置文件的路径
	 * 
	 * @param path - 文件路径
	 */
	public void setConfigPath(String path)
	{
		confPath = path;
	}
	
	/**
	 * 根据HTML的名字对xhtml命名
	 * 
	 * @param name - 以此为基础文件名
	 * @return - 以输入的name基础生成的文件名
	 */
	protected String createXhtmlName(String name)
	{
		return name+"-to-.xhtml";
	}
	

}
