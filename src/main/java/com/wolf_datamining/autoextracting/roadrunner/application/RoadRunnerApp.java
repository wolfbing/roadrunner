package com.wolf_datamining.autoextracting.roadrunner.application;

public interface RoadRunnerApp {
	
	/**
	 * 生成包装器
	 * 
	 * @param fileDir - 用于生成包装器的文件所在的目录（文件夹）
	 * @param outDir - 生成的包装器存放的目录（文件夹）
	 */
	public void generateWrapper(String fileDir, String outDir);
	
	/**
	 * 从单文件中抽取
	 * 
	 * @param wrapperPath - 包装器文件的路径
	 * @param filePath - 待抽取的文件的路径
	 * @param resultDir - 抽取结果存放的路径
	 * 
	 */
	public void extract(String wrapperPath,String filePath,String resultDir);
	
	/**
	 * 批量抽取
	 * 
	 * @param wrapperPath - 包装器所在路径
	 * @param inputDir - 待抽取的文件所在的目录（文件夹）
	 * @param resultDir - 抽取结果存放的目录（文件夹）
	 */
	public void extractAll(String wrapperPath,String inputDir,String resultDir);

}
