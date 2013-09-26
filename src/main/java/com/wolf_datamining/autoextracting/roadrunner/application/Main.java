package com.wolf_datamining.autoextracting.roadrunner.application;

import com.wolf_datamining.autoextracting.roadrunner.preprocess.Html2XhtmlByNeko;

/**
 * 
 * 从一堆html文件到最终的生成包装器并抽取，可以：
 * 
 * 1. 手动执行转换，即先将HTML转换成xhtml，再进行抽取相关操作
 * 
 * 2. 自动执行转换，即输入HTML文件，在抽取操作内部完成转换，不需关心转换问题
 * 
 * 
 * 
 * @author Wolf, 2013-8-1
 *
 */


public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
		
		////*******************************************
		////********* 抽取操作    **************
		////*******************************************
		
//		ExtractFromHtml extracter = new ExtractFromHtml();
		ExtractFromXhtml extracter = new ExtractFromXhtml();
		// 设置输入文件的编码格式
		extracter.setInputFileEncoding("gb2312");
		//设置配置文件路径
		extracter.setConfigPath("path");
		
		// 生成包装器
		extracter.generateWrapper("C:/Users/Admin/Desktop/jd-标注更小/", "newtest");
		
		// 从一张页面抽取
		// 参数1：包装器路径
		// 参数2：输入文件路径，包含文件名
		// 参数3：输出文件路径，不包含文件名
//		extracter.extract("G:/datamining/Projects/roadrunnertest/output/newtest/newtest00.xml",
//				"C:/Users/Admin/Desktop/jd-标注更小/1.html",
//				"G:/datamining/Projects/roadrunnertest/output/newtest/");
		 
		// 批量抽取
		// arg1: 包装器路径
		// arg2: 输入文件路径，到文件所在的文件夹
		// arg3: 输出文件路径，到输出文件所在的文件夹
//		extracter.extractAll("G:/datamining/Projects/roadrunnertest/output/newtest/newtest00.xml",
//				"C:/Users/Admin/Desktop/jd-标注更小/", "G/datamining/Projects/roadrunnertest/output/newtest/");
		
		
		
		
		////*******************************************
		////**************** 转换操作  ******************
		////*******************************************
		
		
		//  首先从html到xhtml的文件转换
		//  使用nekohtml
		Html2XhtmlByNeko converter = new Html2XhtmlByNeko(); 
		// 设置输出文件编码
		converter.setOutputEncoding("utf8"); // 输出编码的设置，如果不设置将采用输入编码
		// 设置输入文件的编码
		converter.setInputEncoding("utf8"); // 如果HTML没有声明编码，这个一定要设置，否则不需要设置
		// 单文件转换
		// arg1: 输入文件路径，包含文件名
		// arg2: 输出文件路径，包含文件名
		converter.convert("C:/Users/Admin/Desktop/test3.html", 
				"C:/Users/Admin/Desktop/test33.xhtml");
		// 批量转换
		// arg1: 输入文件路径，到文件夹
		// arg2: 输出文件路径，到文件夹
		converter.convertAll("C:/Users/Admin/Desktop/OceanSoft的相似页面/相似页面3/",
				"C:/Users/Admin/Desktop/result2/");
		
	
		
		System.out.println("============= END ===============");
		
		
		
		
		
		
		
	}

}
