package uiuc.cs440.hw4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.meta.Bagging;
import weka.core.Instances;


public class SetUp {

	public static Instances data;
	public HashMap accuracy_train;
	public HashMap accuracy_test;
	public Instances accuracyVsN_2;
	public MultilayerPerceptron mp;
	public Classifier  svm_classifier;
	public Instances  trainingData;
	public Instances  testingData;
	
	
	
	SetUp (String filename) throws IOException{
		FileReader reader = new FileReader(filename);
		
		BufferedReader br = new BufferedReader(reader);
		
		data = new Instances(br);
		accuracy_train = new HashMap();
		accuracy_test = new HashMap();
		
		reader.close();
	}
	/**
	 * @param args
	 * @return 
	 * @throws Exception 
	 * @throws IOException 
	 */
	public void classify() throws Exception{
		data.setClassIndex(data.numAttributes()-1);
		/*
		 * Train a Multilayer Perceptron (use the MultilayerPerceptron class and use the
		 * default configuration that is set by the no argument constructor) on the first 80%
		 * of instances (floor(0.8*numInstances)).*/
		
		MultilayerPerceptron mp = new MultilayerPerceptron();
		Instances trainingData = new Instances( data, 0, (int)( data.numInstances()*0.8 ));
		mp.buildClassifier(trainingData);
		/*
		 * Test the classifier on the rest of the examples and report the accuracy (percentage
of correctly classified examples).*/
		Instances testingData = new Instances( data,
											   (int)(data.numInstances()*0.8),
											   data.numInstances() - (int)(data.numInstances()*0.8)  
											 );
		test(mp, testingData);
	}
	
	/**
	 * @param args
	 * @return 
	 * @throws Exception 
	 * @throws IOException 
	 * @N percent of training
	 */
	public void classify(int N) throws Exception{
		double percent = seperateTrainAndTest(N);
		/*
		 *The testing data will be
		 * the last 100-N percent of the instances.
		 *  and report the accuracy of the classifier over the training data and the accuracy over the testing data.
.*/
		testingData = new Instances( data,
											   (int)(data.numInstances()*percent),
											   data.numInstances() - (int)(data.numInstances()*percent)  
											 );
		Instances testingDataOnTrain = new Instances( data,
				   0,
				   (int)(data.numInstances()*percent)  
				 );
		double accuracyTrain = test(mp, testingDataOnTrain);
		double accuracyTest = test(mp, testingData);
		
		accuracy_train.put(N, new Double(accuracyTrain));
		accuracy_test.put(N, new Double(accuracyTest));
		
		
		
	}
	public double seperateTrainAndTest(int N) throws Exception {
		data.setClassIndex(data.numAttributes()-1);
		/*
		 * Train the classifier on the first N percent of instances. 
		 */
		double percent = ((double)N)/100;
		mp = new MultilayerPerceptron();
		trainingData = new Instances( data, 0, (int)( data.numInstances()* percent ));
		mp.buildClassifier(trainingData);
		return percent;
	}
	
	public void writefile( HashMap<Double,Double> atrain , HashMap<Double,Double> atest, String filestr ){
			
		try {
			 
 
			File file = new File(filestr);
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			
			//sort hashmap
			Map<Double, Double> accur_train = new TreeMap<Double, Double>( atrain );
			Map<Double, Double> accur_test = new TreeMap<Double, Double>( atest );
		
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			writeAttributes(accur_train, bw);
			bw.write("@data\n");
			
			writeData(accur_train, bw);
			writeData(accur_test, bw);
			//bw.write(content);
			bw.close();
 
			System.out.println("Done");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
		
	}
	public void writeData(Map<Double, Double> accur_train, BufferedWriter bw)
			throws IOException {
		// Get a set of the entries
		Set set = accur_train.entrySet();
		// Get an iterator
		Iterator i = set.iterator();
		// Display elements			
		  while( (i.hasNext()) ) {
		     Map.Entry me = (Map.Entry)i.next();
		     System.out.print(me.getKey() + ": ");
		     System.out.println(me.getValue());
		     
		     bw.write(me.getValue()+"");
		     if( i.hasNext() ){
		    	 bw.write(",");
		    	 
		     }
		  }
		bw.write("\n");
	}
	public void writeAttributes(Map<Double, Double> accur_train,
			BufferedWriter bw) throws IOException {
		// Get a set of the entries
		Set set = accur_train.entrySet();
		// Get an iterator
		Iterator i = set.iterator();
		// Display elements
		
		bw.write("@relation AccuracyVsN_2\n");
		  while(i.hasNext()) {
		     Map.Entry me = (Map.Entry)i.next();
		     
		     bw.write("@attribute "+me.getKey()+" numeric\n");
		     
		  }
	}
	
	
	public double test(MultilayerPerceptron mp, Instances testingData)
			throws Exception {
		int correct = 0;
		int incorrect = 0;
		for ( int i = 0 ; i<testingData.numInstances() ;i++ ){
			double assignedClass = mp.classifyInstance(testingData.instance(i));
			double originalClass = testingData.instance(i).classValue();
			if( assignedClass == originalClass )
				correct++;
			else
				incorrect++;
		}
		
		double accuracy = 100*(double)correct/(correct+incorrect);
		System.out.printf("%f\n", accuracy );
		
		return accuracy;
	}
	
	public void nCrossValidation( Instances data ,int [] N ) throws Exception{
		//data contains the full dataset we wann create train/test sets from
		int seed = 5;          // the seed for randomizing the data
		int runs = N.length;
		System.out.print("Problem 1 b:\n");
		System.out.print("Acuracy:\n");
		 for (int i = 0; i < runs; i++) {
		   // time start
		   long startTime = System.currentTimeMillis();
			 
		   //seed = i+1;  // every run gets a new, but defined seed value
		 
		   // randomize the data
			 int folds = N[i];         // the number of folds to generate, >=2
			 	
			 nFoldSingleRun(data, seed, folds);
			 
			 
			 long endTime   = System.currentTimeMillis();
			 long totalTime = endTime - startTime;
			 System.out.println("time:\n"+totalTime+"\n");
		 } 
		
		
		 
		
	}
	public void nFoldSingleRun(Instances data, int seed, int folds)
			throws Exception {
		Random rand = new Random(seed);   // create seeded number generator
		 Instances randData = new Instances(data);   // create copy of original data
		 randData.randomize(rand);         // randomize data with number generator
		 
		 //In case your data has a nominal class and you wanna perform stratified cross-validation
		 randData.stratify(folds);
 
		 //  generate the folds
		 //mp = new MultilayerPerceptron();
		 Evaluation evaluation = new Evaluation(randData);
		 evaluation.crossValidateModel(mp, randData, folds, rand);
		 //System.out.println(evaluation.toSummaryString("\nResults\n======\n", false));
		 System.out.print(( evaluation.correct()/(evaluation.correct()+evaluation.incorrect())*100.0 )+"\n");
		 
		 //Instances train = randData.trainCV(folds, n, rand);
//		 for (int n = 0; n < folds; n++) {
//			 Instances train = randData.trainCV(folds, n, rand);
//			 Instances test = randData.testCV(folds, n);
//			 
//			 // further processing, classification, etc.
//			 
//			 
//			 
//			 Evaluation evaluation = new Evaluation(train);
//			 //evaluation.crossValidateModel(mp, test, folds, new Random(1));
//			 evaluation.evaluateModel(mp, test);
//			 System.out.println(evaluation.toSummaryString("\nResults\n======\n", false));
//			 
////				 double accuracyNCV = test(this.mp, test);
////				 System.out.print(accuracyNCV+"\n");
//			 }
	}
	
	
	public void SVM() throws Exception{
		
		SMO svm = new SMO();
		PolyKernel k = new PolyKernel();
		
/*
 * setting polynomials, see post 224
 * 
 */
		// Linear kernel
		k.setExponent(1);
		k.setUseLowerOrder(false);
		svm.setKernel(k);
		svmClassifier(svm);

		// quadratic kernel
		k.setExponent(2);
		k.setUseLowerOrder(false);
		svm.setKernel(k);
		svmClassifier(svm);
		
		// Nonhomogeneous quadratic kernel
		k.setExponent(2);
		k.setUseLowerOrder(true);
		svm.setKernel(k);
		svmClassifier(svm);
				
		// Homogeneous cubic kernel
		k.setExponent(3);
		k.setUseLowerOrder(false);
		svm.setKernel(k);
		svmClassifier(svm);
		
	}
	
	
	
	public void svmClassifier(SMO svm) throws Exception {
		try {
            
			
			
			//use the classifier that you want
            svm_classifier = (Classifier) svm;
                   
            //build the classifier for the training instances
            svm_classifier.buildClassifier(this.trainingData);
                   
            //Evaluating the classifer
            Evaluation eval = new Evaluation(this.data);
            eval.evaluateModel(svm_classifier,this.data);
                   
            //printing the summary of training
            String strSummary = eval.toSummaryString();
            System.out.println("-----------TRAINING SUMMARY---------");
            System.out.println(strSummary);
            System.out.println("------------------------------------");
                   
            //Serializing the classsifier object in to a file
            weka.core.SerializationHelper.write("SMO.model",svm_classifier);
          }catch(Exception ex) {
      //        Logger.getLogger(Weka.class.getName()).log(Level.SEVERE, null, ex);
          }
		
	}
	
	public void neuralNetworkTopologies(String hidenlayer) throws Exception{
		int seed = 1;          // the seed for randomizing the data
		 
		   // randomize the data
		int folds = 10;         // the number of folds to generate, >=2
		mp.setHiddenLayers(hidenlayer);
		//this.nFoldSingleRun(data, seed, folds);
		Random rand = new Random(seed);   // create seeded number generator
		 Instances randData = new Instances(trainingData);   // create copy of original data
		 randData.randomize(rand);         // randomize data with number generator
		 
		 //In case your data has a nominal class and you wanna perform stratified cross-validation
		 randData.stratify(folds);
		 
		 //  generate the folds
		 //mp = new MultilayerPerceptron();
		 mp.buildClassifier(randData);
		 Evaluation evaluation = new Evaluation(testingData);
		 evaluation.crossValidateModel(mp, testingData, folds, rand);
		 //System.out.println(evaluation.toSummaryString("\nResults\n======\n", false));
		 System.out.print(( evaluation.correct()/(evaluation.correct()+evaluation.incorrect())*100.0 )+"\n");
		 
	}
	
	
	public void baggingWithNCrossValidation(String hidenlayer) throws Exception{
		mp= new MultilayerPerceptron();
		int seed = 1;          // the seed for randomizing the data
		 
		// randomize the data
		int folds = 10;         // the number of folds to generate, >=2
		
		Bagging bagg = new Bagging();
		
		Random rand = new Random(seed);   // create seeded number generator
		 Instances randData = new Instances(data);   // create copy of original data
		 randData.randomize(rand);         // randomize data with number generator
		 
		 //In case your data has a nominal class and you wanna perform stratified cross-validation
		 randData.setClassIndex((randData.numAttributes()-1));
		 randData.stratify(folds);
		 
		 mp.setHiddenLayers(hidenlayer);
		 mp.buildClassifier(randData);
		 bagg.setClassifier(mp);
		 //  generate the folds
		 //mp = new MultilayerPerceptron();
		 Evaluation evaluation = new Evaluation(randData);
		 evaluation.crossValidateModel(bagg, randData, folds, rand);
		 System.out.println(evaluation.toSummaryString("\nResults\n======\n", false));
		
		
		
		
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.printf("hello world\n");
		SetUp test = new SetUp("DataSet1.arff");
		test.classify();
		System.out.print("Part2:\n");
		
		
		//Pr1 a
		
		SetUp glass = new SetUp("weka-3-6-10/data/glass.arff");
		System.out.print(glass.data.relationName() );
		glass.classify(10);
		glass.classify(30);
		glass.classify(50);
		glass.classify(70);
		glass.classify(90);
		
		glass.writefile(glass.accuracy_train, glass.accuracy_test, "AccuracyVsNTrainTest.arff");
		
		
		//Pr b
		int[] n = new int[5];
		n[0] = 2;
		n[1] = 5;
		n[2] = 10;
		n[3] = 15;
		n[4] = 20;
		
		glass.seperateTrainAndTest(90);
		
		glass.nCrossValidation(glass.data,  n );
		
		//Problem 2
		
		glass.SVM();
		
		//problem 3
		System.out.println("Problem 3");
		glass.neuralNetworkTopologies("2,5,2");
		glass.neuralNetworkTopologies("7,2");
		glass.neuralNetworkTopologies("5,5,3");
		glass.neuralNetworkTopologies("1");
		
		//problem 4
		System.out.println("Problem 4");
		glass.baggingWithNCrossValidation("2,5,2");
		glass.baggingWithNCrossValidation("7,1");
		glass.baggingWithNCrossValidation("5,2,3");
		glass.baggingWithNCrossValidation("1,0");
		
		
		

	  

	}

}
