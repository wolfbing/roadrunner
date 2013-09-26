package com.wolf_datamining.autoextracting.roadrunner.preprocess;

public interface Label {
	
	public void label(String inputPath, String outputPath,String option);
	
	public void labelAll(String inputDir, String outputDir,String option);

}
