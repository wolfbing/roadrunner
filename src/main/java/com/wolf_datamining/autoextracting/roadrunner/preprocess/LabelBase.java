package com.wolf_datamining.autoextracting.roadrunner.preprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class LabelBase implements Label {

	public LabelBase() {
		// TODO Auto-generated constructor stub
		
		postfixes = new ArrayList<String>();
		postfixes.addAll(Arrays.asList(".htm",".html",".xhtml"));
		
	}
	
	protected String encoding = "utf8";
	
	protected List<String> postfixes;// = {".htm",".html",".xhtml"};
	
	protected boolean filter = true;
	
	/**
	 * 设置待转换的文件编码
	 * @param encode
	 * @return
	 */
	public LabelBase setInputEncoding(String encode)
	{
		this.encoding = encode;
		return this;
	}
	
	/**
	 * 设置是否进行文件名过滤，默认进行文件名过滤，".htm",".html",".xhtml"能够通过过滤
	 * @param f
	 */
	public void setHaveFilter(boolean f)
	{
		this.filter = f;
	}
	
	/**
	 * 设置通过过滤的文件的后缀名
	 * @param postfixList
	 */
	public void setFileFilter(List<String> postfixList)
	{
		this.postfixes.clear();
		for(String fix:postfixList)
		{
			this.postfixes.add(fix.toLowerCase().trim());
		}
	}
	
	/**
	 * 添加能够通过过滤的文件的后缀名
	 * @param postfixList
	 */
	public void addFileFilter(List<String> postfixList)
	{
		for(String fix:postfixList)
		{
			this.postfixes.add(fix.toLowerCase().trim());
		}
	}
	
	/**
	 * 判断文件名是否合理，即是否能通过过滤
	 * @param name
	 * @return
	 */
	protected boolean validFileName(String name)
	{
		if(filter)
		{
			for(String fix: postfixes)
			{
				if(name.trim().toLowerCase().endsWith(fix))
				{
					return true;
				}
			}
			return false;
		}
		else
		{
			return true;
		}
		
	}
	
	


}
