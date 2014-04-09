package uiuc.cs440.hw4;

import weka.core.Instances;

public class NeuralNetworks {
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		
		
		
		SetUp glass = new SetUp("weka-3-6-10/data/glass.arff");
		System.out.print(glass.data.relationName() );
		
		//problem 3
		
		glass.neuralNetworkTopologies("2,5,2");
		glass.neuralNetworkTopologies("7,2");
		glass.neuralNetworkTopologies("5,5,3");
		glass.neuralNetworkTopologies("1");
		
		
		
		

	  

	}
}
