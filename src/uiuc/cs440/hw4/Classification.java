package uiuc.cs440.hw4;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;




public class Classification {
	
	public double[] x;
	public double[] y;
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.printf("hello world\n");
		SetUp test = new SetUp("DataSet1.arff");
		test.classify();
		System.out.print("Part2:\n");
		
		
		//Pr1 a
		int num_points = 5;
		SetUp glass = new SetUp("weka-3-6-10/data/glass.arff");
		System.out.print(glass.data.relationName() );
		glass.classify(10);
		glass.classify(30);
		glass.classify(50);
		glass.classify(70);
		glass.classify(90);
		
		glass.writefile(glass.accuracy_train, glass.accuracy_test, "AccuracyVsNTrainTest.arff");
		
		Plot2DPanel plot = new Plot2DPanel();
		 
		// define the legend position
		plot.addLegend("SOUTH");
 
		// add a line plot to the PlotPanel
		Set set = glass.accuracy_train.entrySet();
		// Get an iterator
		Iterator i = set.iterator();
		
		double[] x = { 10,30,50,70,90 };
		double[] y = new double[num_points];
		int j=0;
		// Display elements			
		  while( i.hasNext() ) {
		     Map.Entry me = (Map.Entry)i.next();
		     
		     //y[j]= (double)me.getValue();
		    j++;	 
		  }
		  
		
		plot.addLinePlot("my plot",x ,y );
 
		// put the PlotPanel in a JFrame like a JPanel
		JFrame frame = new JFrame("a plot panel");
		frame.setSize(600, 600);
		frame.setContentPane(plot);
		frame.setVisible(true);
		

		

	  

	}
}
