package com.wolf_datamining.autoextracting.roadrunner.application;

public class ExtracterFactory {

	public ExtracterFactory() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 生成从xhtml文件生成包装器以及抽取数据的抽取器
	 * @return
	 */
	public RoadRunnerApp createExtracterUsingXhtml()
	{
		RoadRunnerApp extracter = new ExtractFromXhtml();
		return extracter;
	}
	
	/**
	 * 生成从HTML文件生成包装器以及抽取数据的抽取器
	 * @return
	 */
	public RoadRunnerApp createExtracterUsingHtml()
	{
		RoadRunnerApp extracter = new ExtractFromHtml();
		
		return extracter;
	}
	
	

}
