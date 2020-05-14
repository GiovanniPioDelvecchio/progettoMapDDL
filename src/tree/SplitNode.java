package progettoMapDDL.src.tree;

import progettoMapDDL.src.data.Attribute;
import progettoMapDDL.src.data.Data;

public abstract class SplitNode extends Node {
	// Classe che colelzione informazioni descrittive dello split
	protected class SplitInfo{
		Object splitValue;
		int beginIndex;
		int endIndex;
		int numberChild;
		String comparator="=";
		
		SplitInfo(Object splitValue,int beginIndex,int endIndex,int numberChild) {
			
			this.splitValue=splitValue;
			this.beginIndex=beginIndex;
			this.endIndex=endIndex;
			this.numberChild=numberChild;
		}
		SplitInfo(Object splitValue,int beginIndex,int endIndex,int numberChild, String comparator) {
			this.splitValue=splitValue;
			this.beginIndex=beginIndex;
			this.endIndex=endIndex;
			this.numberChild=numberChild;
			this.comparator=comparator;
		}
		
		int getBeginindex() {
			return beginIndex;			
		}
		int getEndIndex() {
			return endIndex;
		}
		 Object getSplitValue() {
			return splitValue;
		}
		public String toString() {
			return "child " + numberChild +" split value"+comparator+splitValue + "[Examples:"+beginIndex+"-"+endIndex+"]";
		}
		 String getComparator() {
			return comparator;
		}
	
		
	}

	private Attribute attribute;	

	protected SplitInfo mapSplit[];
	
	private double splitVariance;
		
	public abstract void setSplitInfo(Data trainingSet,int beginExampelIndex, int endExampleIndex, Attribute attribute);
	
	abstract int testCondition (Object value);
	
	public SplitNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute){
			super(trainingSet, beginExampleIndex,endExampleIndex);
			this.attribute=attribute;
			trainingSet.sort(attribute, beginExampleIndex, endExampleIndex); // order by attribute
			setSplitInfo(trainingSet, beginExampleIndex, endExampleIndex, attribute);
						
			//compute variance
			splitVariance=0;
			for(int i=0;i<mapSplit.length;i++){
					double localVariance = new LeafNode(trainingSet, mapSplit[i].getBeginindex(), mapSplit[i].getEndIndex()).getVariance();
					splitVariance+=(localVariance);
			}
	}
	
	public Attribute getAttribute() {
		
		return attribute;
	}
	
	public double getVariance() {
		
		return splitVariance;
	}
	
	public int getNumberOfChildren() {
		 
		return mapSplit.length;
	}
	
	public SplitInfo getSplitInfo(int child) {
		
		return mapSplit[child];
	}

	
	public String formulateQuery() {
		
		String query = "";
		for(int i=0;i<mapSplit.length;i++)
			query+= (i + ":" + attribute + mapSplit[i].getComparator() +mapSplit[i].getSplitValue())+"\n";
		return query;
	}
	
	public String toString() {
		
		String v= "SPLIT : attribute=" +attribute +" Nodo: "+ super.toString()+  " Split Variance: " + getVariance()+ "\n" ;
		
		for(int i=0;i<mapSplit.length;i++){
			v+= "\t"+mapSplit[i]+"\n";
		}
		
		return v;
	}
}
