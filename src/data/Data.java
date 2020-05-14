package progettoMapDDL.src.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;




public class Data {
	
	private Object data [][];
	private int numberOfExamples;
	private Attribute explanatorySet[];
	private ContinuousAttribute classAttribute;
	
	public Data(String fileName) throws TrainingDataException {
		
		File inFile = new File (fileName);
		Scanner sc;
		
		try {
			
			sc = new Scanner (inFile); //lancia filenotfound
			
		} catch(FileNotFoundException e) {
			
			throw new TrainingDataException(e);
		}
		
		
		
		  
		String line = sc.nextLine();
	      
	      
	    if (!line.contains("@schema")) {
	    	  
	    	sc.close();
	    	throw new TrainingDataException("Schema is missing");	//schema mancante
	    }
	    	  
	    String s[]=line.split(" ");

		//popolare explanatory Set 
	    //@schema 4
	    int explanatorySetSize;
	    
		try {
			
			explanatorySetSize = Integer.parseInt(s[1]);
			
		} catch(NumberFormatException e) {
			
			sc.close();
			throw new TrainingDataException("Bad acquisition from file", e);
		}
		
 
		if(explanatorySetSize>0) {

			explanatorySet = new Attribute[explanatorySetSize];

		} else {

			//se la dimensione dichiarata non è appropriata

			sc.close();

			throw new TrainingDataException("Inappropriate Schema dimension!");

		}
		
		
		short iAttribute=0;
	    line = sc.nextLine();
	      
	    int targetFlag = 0;
	      
	    while (!line.contains("@data")) {		//bisogna comprendere se è necessario contemplare il caso in cui 
	    	  									//explanatorySet.length sia minore del numero effettivo di attributi
	    	
	    	s = line.split(" ");
	    	
	    	if (s.length != 0) {
	    		
		    	if (s[0].equals("@desc")) { // aggiungo l'attributo allo spazio descrittivo
		    		  
	    			//@desc motor discrete A,B,C,D,E  
		    		String discreteValues[]=s[2].split(",");
		    		
		    		if (iAttribute < explanatorySet.length) {
		    			
		    			explanatorySet[iAttribute] = new DiscreteAttribute(s[1],iAttribute, discreteValues);
		    			
		    		} else {
		    			
		    			sc.close();
		    			throw new TrainingDataException("The actual no. of Attributes is less than declared!");
		    		}
		    		
		    		  
		    	} else if (s[0].equals("@target")) {
		    	  
		    		classAttribute=new ContinuousAttribute(s[1], iAttribute);
		    		targetFlag++;
		    	} 
	    	  
	    	  
		    	iAttribute++;
		    	
	    	}
	    	  

	    	line = sc.nextLine();
	    }
	      
	    if (targetFlag != 1 || iAttribute != explanatorySet.length + 1) {	
	    	
	    	//mancanza di target o schema di dimensioni diverse da quelle dichiarate 
	    	sc.close();
	    	throw new TrainingDataException("the target attribute is missing or the schema is empty");
	    }
		      
		//avvalorare numero di esempi
	    //@data 167
		numberOfExamples = Integer.parseInt(line.split(" ")[1]);

		if(numberOfExamples<=0) {

			// se viene dichiarato un numero invalido di esempi

			sc.close();
			throw new TrainingDataException("Invalid number of Examples!");
		}
	      
	    //popolare data
	    data = new Object[numberOfExamples][explanatorySet.length+1];
	      
	    short iRow=0;
	      
	    while (sc.hasNextLine() && iRow < numberOfExamples) {
	    	  
	    	line = sc.nextLine();
	    	
	    	if (!line.isEmpty()) {
	    		
		    	// assumo che attributi siano tutti discreti
		    	s=line.split(","); //E,E,5,4, 0.28125095
		    	
		    	if(s.length == explanatorySet.length + 1) {
		    		
			    	for (short jColumn=0; jColumn<s.length-1; jColumn++) {
			    		
			    		
		    			boolean member = false;
		    			for (int iter = 0; !member && iter < ((DiscreteAttribute)explanatorySet[jColumn]).getNumberOfDistinctValues(); iter++) {
		    				  
		    				if ( ((DiscreteAttribute)explanatorySet[jColumn]).getValue(iter).equals(s[jColumn])) {
		    					  
		    				 member = true;
		    				} 
		    			}
			    		  
			    		if (s[jColumn].equals("") || member == false) {	//valore non presente o non facente parte del dominio dell'attributo
			    			 
			    			sc.close();
			    			throw new TrainingDataException("A value in the file is out of domain");
			    		}
			    		
			    		
			    		data[iRow][jColumn]=s[jColumn];
			    		
			    	}
			    	
			    	try {
			    		
			    		data[iRow][s.length-1] = Double.parseDouble(s[s.length-1]);
			    		
			    	} catch (NumberFormatException e) {
			    		
			    		sc.close();
			    		throw new TrainingDataException("Class attribute parsification errors");
			    	}
			    	
			    	iRow++;
		    		    	  
		    
			    } else {
			    		
			    	sc.close();
			    	throw new TrainingDataException("Bad example declaration at line: " + line);
			    }
	    	}	
	    }
	      
	    if (iRow != numberOfExamples) {
	    	  
	    	sc.close();
	    	throw new TrainingDataException("Bad number of Rows");	//trainingSet vuoto
	    }
	      
		sc.close();
	}
	
	public String toString() {
		
		String value="";
		
		for (int i=0;i<numberOfExamples;i++) {
			
			for (int j=0; j<explanatorySet.length; j++) {
	
				value+=data[i][j]+",";
			}
				
			value += data[i][explanatorySet.length] + "\n";
		}
		return value;
	}
	
	public int getNumberOfExamples() {
		
		return numberOfExamples;
	}
	
	public int getNumberOfExplanatoryAttributes() {
		
		return explanatorySet.length;
	}
	

	/*
	 * dato l'indice di riga, restituisce il valore (il Double)
	 */
	public Double getClassValue(int exampleIndex) 	{
		if(exampleIndex >= 0 && exampleIndex < getNumberOfExamples()) 
		{
			return (double)data[exampleIndex][classAttribute.getIndex()];
			
		}else {
			
			return 0.0; //da sostituire con un'eccezione
		} 
	}

  
	/*
	 * restituisce il valore di un attributo dato indice di riga e indice dell'attributo
	 */
	public Object getExplanatoryValue(int exampleIndex, int attributeIndex) 	{
		if( (exampleIndex >= 0 && exampleIndex < getNumberOfExamples() ) && (attributeIndex < getNumberOfExplanatoryAttributes() ) ) 
		{
			return data[exampleIndex][attributeIndex];
		}
		
		return data[exampleIndex][attributeIndex];  //da sostituire con un'eccezione
	}
	

	/*
	 * restituisce l'attributo in indice index all'interno dello schema 
	 */
	public Attribute getExplanatoryAttribute(int index) 	{
		if(index < getNumberOfExplanatoryAttributes()) 
		{
			return explanatorySet[index];
		}
		
		return explanatorySet[index]; //da sostituire con un'eccezione
	}
	

	/*
	 * 
	 */
	public ContinuousAttribute getClassAttribute()	{
    
		return classAttribute;
	}
	
	public void sort(Attribute attribute, int beginExampleIndex, int endExampleIndex) {
		
		quicksort(attribute, beginExampleIndex, endExampleIndex);
	}
	
	// scambio esempio i con esempi oj
	private void swap(int i, int j) {
		
		Object temp;
		
		for (int k = 0; k < getNumberOfExplanatoryAttributes() + 1; k++) {
			
			temp = data[i][k];
			data[i][k] = data[j][k];
			data[j][k] = temp;
		}
	}
	
	/*
	 * Partiziona il vettore rispetto all'elemento x e restiutisce il punto di separazione
	 */
	private int partition(DiscreteAttribute attribute, int inf, int sup) {
		int i,j;
	
		i=inf; 
		j=sup; 
		int	med=(inf+sup)/2;
		String x=(String)getExplanatoryValue(med, attribute.getIndex());
		swap(inf,med);
	
		while (true) {
			
			while (i<=sup && ((String)getExplanatoryValue(i, attribute.getIndex())).compareTo(x)<=0) { 
				
				i++; 
			}
		
			while ( ( (String) getExplanatoryValue(j, attribute.getIndex())).compareTo(x) > 0) {
				
				j--;
			}
			
			if(i<j) {
				
				swap(i,j);
			}else { 
				break;
			}
		}
		
		swap(inf,j);
		return j;
	}
	
	/*
	 * Algoritmo quicksort per l'ordinamento di un array di interi A
	 * usando come relazione d'ordine totale "<="
	 * @param A
	 */
	private void quicksort(Attribute attribute, int inf, int sup) {
		
		if(sup >= inf) {
			
			int pos;
			
			pos = partition((DiscreteAttribute)attribute, inf, sup);
					
			if ((pos-inf) < (sup-pos+1)) {
				
				quicksort(attribute, inf, pos-1); 
				quicksort(attribute, pos+1,sup);
			} else {
			
				quicksort(attribute, pos+1, sup); 
				quicksort(attribute, inf, pos-1);
			}	
		}
	}
	
	/*
	public static void main(String args[])throws FileNotFoundException{
		
		Data trainingSet=new Data("servo.dat");
		System.out.println(trainingSet);
		
		
		for (int jColumn=0; jColumn<trainingSet.getNumberOfExplanatoryAttributes(); jColumn++) {
			
			System.out.println("ORDER BY "+trainingSet.getExplanatoryAttribute(jColumn));
			trainingSet.quicksort(trainingSet.getExplanatoryAttribute(jColumn),0 , trainingSet.getNumberOfExamples() - 1);
			System.out.println(trainingSet);
		}	
	}
	*/
}