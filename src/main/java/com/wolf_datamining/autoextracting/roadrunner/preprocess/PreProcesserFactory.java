package com.wolf_datamining.autoextracting.roadrunner.preprocess;

/**
 * 预处理工厂类
 * <br>用于产生各种 转换工具
 * @author Admin
 *
 */
public class PreProcesserFactory {

	public PreProcesserFactory() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 产生一个以xpath表达式为基础的标注工具
	 * @return
	 */
	public Label createLabelToolByXPath()
	{
		return new LabelByXPath();
	}
	
	/**
	 * 产生一个以jsoup表达式为基础的标注工具
	 * @return
	 */
	public Label createLabelToolByJsoup()
	{
		return new LabelByJsoup();
	}
	
	/**
	 * 产生一个以html2xhtml工具为基础的html转换为xhtml的工具,仅限在2windows平台使用
	 * @return
	 */
	public Html2Xhtml createHtml2XhtmlByH2X()
	{
		return new Html2XhtmlByH2X();
	}
	
	/**
	 * 产生一个以nekohtml为基础的html转换为xhtml的工具，推荐为首选HTML-xhtml转换工具
	 * @return
	 */
	public Html2Xhtml createHtml2XhtmlByNeko()
	{
		return new Html2XhtmlByNeko();
	}
	
	
	
	
	

}
