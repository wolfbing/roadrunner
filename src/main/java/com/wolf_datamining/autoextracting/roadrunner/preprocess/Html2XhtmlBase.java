package com.wolf_datamining.autoextracting.roadrunner.preprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Html2XhtmlBase implements Html2Xhtml {
	
	
	/**
	 * 输入文件的编码，gb2312,utf8,shift_jis
	 */
	protected String encoding;
	
	protected String inputEncoding;
	
	protected boolean filter;
	
	protected List<String> postfixes;
	
	
	public void setHaveFilter(boolean f)
	{
		filter = f;
	}


	
	public Html2XhtmlBase() {
		// TODO Auto-generated constructor stub
		
		this.encoding = "utf8";
		this.inputEncoding = "utf8";
		this.filter= true;
		postfixes = new ArrayList<String>();
		String [] fixArr = {".html",".htm",".xhtml"};
		postfixes.addAll(Arrays.asList(fixArr));
		
	}

	@Override
	public Html2Xhtml setOutputEncoding(String encoding) {
		// TODO Auto-generated method stub
		this.encoding = encoding;
		return this;
	}
	
	@Override
	public Html2Xhtml setInputEncoding(String encoding)
	{
		this.inputEncoding = encoding;
		return this;
	}
	
	
	/**
	 * 判断文件名是否合适，即是否能通过过滤。
	 * @param name
	 * @return
	 */
	public boolean validName(String name) {
		// TODO Auto-generated method stub
		
		if(this.filter)
		{
			for(String postfix: postfixes)
			{
				// 因为采用结束字符的比较，所以无论name是路径还是url都可以进行判断
				if(name.toLowerCase().trim().endsWith(postfix))
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
	
	/**
	 * 设置可以通过过滤的文件的后缀名
	 * @param li_postfix
	 */
	public void setFileFilter(List<String> li_postfix)
	{
		postfixes.clear();
		for(String fix: li_postfix)
		{
			postfixes.add(fix.trim().toLowerCase());
		}
	}
	
	/**
	 * 增加可以通过过滤的文件的后缀名
	 * @param postfixes
	 */
	public void addFileFilter(List<String> postfixes)
	{
		for(String fix: postfixes)
		{
			postfixes.add(fix.trim().toLowerCase());
		}

	}
	
	/**
	 * 依据原文件名产生xml的文件名
	 * @param name
	 * @return
	 */
	protected String createXhtmlName(String name)
	{
		return name+"-to-.xhtml";
	}
	
		


}
