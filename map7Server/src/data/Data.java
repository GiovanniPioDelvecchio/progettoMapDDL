package data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import database.DatabaseConnectionException;
import database.DbAccess;
import database.EmptySetException;
import database.Example;
import database.TableData;
import database.TableSchema;
import database.Column;

public class Data {
	
	private List<Example> data = new ArrayList<>();
	private int numberOfExamples;
	private List<Attribute> explanatorySet;
	private ContinuousAttribute classAttribute;
	
	public Data(String tableName) throws TrainingDataException {
		
		DbAccess db = new DbAccess();
		try {
			db.initConnection();
		} catch (DatabaseConnectionException e) {
			System.out.println("Sono qui 1");
			throw new TrainingDataException(e.getMessage());
		}
		TableSchema schema;
		TableData data;
		explanatorySet = new LinkedList<>();
		try {
			schema = new TableSchema(db, tableName);
			data = new TableData(db);
			if(schema.getNumberOfAttributes() == 0) {
				throw new TrainingDataException("Table not found");
			}
			if(schema.getNumberOfAttributes() < 2) {
				throw new TrainingDataException("Attributes must be at least 2");
			}
			if(!schema.getColumn(schema.getNumberOfAttributes() - 1).isNumber()) {
				throw new TrainingDataException("Missing class attribute");
			}
			int index = 0;
			for (Column c : schema) {
				if(c.isNumber()) {
					if(index == schema.getNumberOfAttributes() - 1) {
						classAttribute = new ContinuousAttribute(c.getColumnName(), index);
					} else {
						explanatorySet.add(new ContinuousAttribute(c.getColumnName(), index));
					}
				} else {
					Set<String> set = new TreeSet<>();
					Set<Object> explSet = data.getDistinctColumnValues(tableName, c);
					for(Object o : explSet) {
						set.add((String)o);
					}
					explanatorySet.add(new DiscreteAttribute(c.getColumnName(), index,set));
				}
				index++;
			}
			
			this.data = data.getTransazioni(tableName);
			numberOfExamples = this.data.size();
			
			
			
		} catch (SQLException e) {
			
			System.out.println("Sono qui 2");
			throw new TrainingDataException(e.getMessage());
		} catch (ClassCastException e) {
			
			System.out.println("Sono qui 3");
			throw new TrainingDataException("Error in casting a discrete value");
		} catch (EmptySetException e) {
			
			System.out.println("Sono qui 4");
			throw new TrainingDataException("Empty table");
		} finally {
			try {
				db.closeConnection();
			} catch (DatabaseConnectionException e) {
				System.out.println("Sono qui 5");
				throw new TrainingDataException("Error in closing db connection");
			}
		}
		
	}
	
	public String toString() {
		
		String value="";
		
		for (int i=0;i<numberOfExamples;i++) {
			
			for (int j=0; j<explanatorySet.size(); j++) {
	
				value+=data.get(i).get(j)+",";
			}
				
			value += data.get(i).get(explanatorySet.size() -1) + "\n";
		}
		return value;
	}
	
	/*
	 * dato l'indice di riga, restituisce il valore (il Double)
	 */
	public Double getClassValue(int exampleIndex) 	{
		if(exampleIndex >= 0 && exampleIndex < getNumberOfExamples()) {

			return (double)data.get(exampleIndex).get(classAttribute.getIndex());
		} else {
			
			throw new IndexOutOfBoundsException();
		}
	}

	
	public int getNumberOfExamples() {
		
		return numberOfExamples;
	}
  
	/*
	 * restituisce il valore di un attributo dato indice di riga e indice dell'attributo
	 */
	public Object getExplanatoryValue(int exampleIndex, int attributeIndex) 	{
		if( (exampleIndex >= 0 && exampleIndex < getNumberOfExamples() ) && (attributeIndex < getNumberOfExplanatoryAttributes())) 
		{
			return data.get(exampleIndex).get(attributeIndex);
		}
		
		throw new IndexOutOfBoundsException();
	}
	

	/*
	 * restituisce l'attributo in indice index all'interno dello schema 
	 */
	public Attribute getExplanatoryAttribute(int index) 	{
		if(index < getNumberOfExplanatoryAttributes()) 
		{
			return explanatorySet.get(index);
		}
		
		throw new IndexOutOfBoundsException();
	}


	public ContinuousAttribute getClassAttribute()	{
    
		return classAttribute;
	}
	
	public int getNumberOfExplanatoryAttributes() {
		
		return explanatorySet.size();
	}
	
	
	public void sort(Attribute attribute, int beginExampleIndex, int endExampleIndex){
		quicksort(attribute, beginExampleIndex, endExampleIndex);
	}
	

	// scambio esempio i con esempi oj
	private void swap(int i,int j){

		Example temp = data.get(i);
		data.set(i, data.get(j));
		data.set(j, temp);
	}
	


	/*
	 * Partiziona il vettore rispetto all'elemento x e restiutisce il punto di separazione
	 */
	
	private  int partition(DiscreteAttribute attribute, int inf, int sup){
		int i,j;
	
		i=inf; 
		j=sup; 
		int	med=(inf+sup)/2;
		String x=(String)getExplanatoryValue(med, attribute.getIndex());
		swap(inf,med);
	
		while (true) 
		{
			
			while(i<=sup && ((String)getExplanatoryValue(i, attribute.getIndex())).compareTo(x)<=0){ 
				i++; 
				
			}
		
			while(((String)getExplanatoryValue(j, attribute.getIndex())).compareTo(x)>0) {
				j--;
			
			}
			
			if(i<j) { 
				swap(i,j);
			}
			else break;
		}
		swap(inf,j);
		return j;
	}

	
	
	/*
	 * Partiziona il vettore rispetto all'elemento x e restiutisce il punto di separazione
	 */
	private  int partition(ContinuousAttribute attribute, int inf, int sup){
		int i,j;
	
		i=inf; 
		j=sup; 
		int	med=(inf+sup)/2;
		Double x=(Double)getExplanatoryValue(med, attribute.getIndex());
		swap(inf,med);
	
		while (true) 
		{
			
			while(i<=sup && ((Double)getExplanatoryValue(i, attribute.getIndex())).compareTo(x)<=0){ 
				i++; 
				
			}
		
			while(((Double)getExplanatoryValue(j, attribute.getIndex())).compareTo(x)>0) {
				j--;
			
			}
			
			if(i<j) { 
				swap(i,j);
			}
			else break;
		}
		swap(inf,j);
		return j;
	
	}
	
	/*
	 * Algoritmo quicksort per l'ordinamento di un array di interi A
	 * usando come relazione d'ordine totale "<="
	 * @param A
	 */
	private void quicksort(Attribute attribute, int inf, int sup){
		
		if(sup>=inf){
			
			int pos;
			if(attribute instanceof DiscreteAttribute)
				pos=partition((DiscreteAttribute)attribute, inf, sup);
			else
				pos=partition((ContinuousAttribute)attribute, inf, sup);
					
			if ((pos-inf) < (sup-pos+1)) {
				quicksort(attribute, inf, pos-1); 
				quicksort(attribute, pos+1,sup);
			}
			else
			{
				quicksort(attribute, pos+1, sup); 
				quicksort(attribute, inf, pos-1);
			}
			
			
		}
		
	}
	
}
