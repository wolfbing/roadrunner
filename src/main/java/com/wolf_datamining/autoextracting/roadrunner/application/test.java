package com.wolf_datamining.autoextracting.roadrunner.application;

import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

import com.wolf_datamining.autoextracting.roadrunner.Wrapper;
import com.wolf_datamining.autoextracting.roadrunner.ast.Expression;


public class test {

	public test() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String [] args)
	{
		try {
			Wrapper w = Wrapper.load(new File("G:/datamining/Projects/roadrunnertest/output/test11/test1100.xml"));
			Expression expr = w.getExpression();
			Wrapper w2 = Wrapper.load(new File("C:/Users/Admin/Desktop/test1100.xml"));
			Expression exp2 = w.getExpression();
			
			if(w.equals(w2))
			{
				System.out.println(true);
			}
			else
			{
				System.out.println(false);
			}
			
			
			
			
			
		} catch (IOException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	

}
