package uiuc.cs440.hw4;

import weka.core.Instances;

public class Bagging {
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.printf("hello world\n");
		SetUp test = new SetUp("DataSet1.arff");
		test.classify();
		System.out.print("Part2:\n");
		
		
		//Pr1 a
		
		SetUp glass = new SetUp("weka-3-6-10/data/glass.arff");
		System.out.print(glass.data.relationName() );

		
		//problem 4
		System.out.println("Problem 4");
		glass.baggingWithNCrossValidation("2,5,2");
		glass.baggingWithNCrossValidation("7,2");
		glass.baggingWithNCrossValidation("5,5,3");
		glass.baggingWithNCrossValidation("1");
		
		
		

	  

	}
}
