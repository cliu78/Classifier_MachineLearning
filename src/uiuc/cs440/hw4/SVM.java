package uiuc.cs440.hw4;

import weka.core.Instances;

public class SVM {
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		
		SetUp glass = new SetUp("weka-3-6-10/data/glass.arff");
		System.out.print(glass.data.relationName() );
		
		
	
		
		//Problem 2 
		glass.SVM();
		
		
		
		

	  

	}
}
