package progettoMapDDL.src;
import java.io.FileNotFoundException;

import progettoMapDDL.src.data.Data;
import progettoMapDDL.src.data.TrainingDataException;
import progettoMapDDL.src.tree.RegressionTree;
import progettoMapDDL.src.exceptions.UnknownValueException;
import progettoMapDDL.src.utility.Keyboard;

class MainTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws TrainingDataException{
		
		Data trainingSet;
		
		String fileName;
		
		System.out.println("Training set: ");

		fileName = Keyboard.readWord();
		
		
		
		
		
		//------------------------------------------------
		
		System.out.println("Training set: \n" + fileName);
		
		System.out.println("Starting data acquisition phase!");
		
		try {
			
			trainingSet= new Data(fileName);
			
		} catch (TrainingDataException e) {
		
			System.out.println(e.toString());
			return;
		}
		
		
		
		System.out.println("Starting learning phase!");
		
		RegressionTree tree=new RegressionTree(trainingSet);
		
		tree.printRules();
		
		tree.printTree();
		
		Double outcome = 0.0;
		
		boolean repeatFlag = false;
		
		do {
			
			try {
				
				System.out.println("Starting prediction phase!");
				outcome = tree.predictClass();
				System.out.println(outcome);
				
			} catch (UnknownValueException e) {
				
				System.out.println(e.toString());
				
			} finally {
				
				System.out.println("Would you repeat ? (y/n)");
				
				if (Keyboard.readChar() == 'y') {
					
					repeatFlag = true;
					
				} else {
					
					return;
				}
			}
			
			
		}while(repeatFlag);
		
	}

}
