package tree;

import java.io.Serializable;
import java.util.ArrayList;

import data.Attribute;
import data.Data;
import server.UnknownValueException;



public abstract class SplitNode extends Node implements Comparable<SplitNode> {
	
	// Classe che colelzione informazioni descrittive dello split
	protected class SplitInfo implements Serializable {
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

	ArrayList<SplitInfo> mapSplit;
	
	private double splitVariance;
		
	abstract void setSplitInfo(Data trainingSet,int beginExampelIndex, int endExampleIndex, Attribute attribute);
	
	abstract int testCondition (Object value) throws UnknownValueException;
	
	public SplitNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute){
			super(trainingSet, beginExampleIndex,endExampleIndex);
			this.attribute=attribute;
			trainingSet.sort(attribute, beginExampleIndex, endExampleIndex); // order by attribute
			setSplitInfo(trainingSet, beginExampleIndex, endExampleIndex, attribute);
						
			//compute variance
			splitVariance=0;
			for(SplitInfo si : mapSplit) {
				double localVariance = new LeafNode(trainingSet, si.getBeginindex(), si.getEndIndex()).getVariance();
				splitVariance+=(localVariance);
			}
	}
	
	Attribute getAttribute() {
		
		return attribute;
	}
	
	public double getVariance() {
		
		return splitVariance;
	}
	
	public int getNumberOfChildren() {
		 
		return mapSplit.size();
	}
	
	SplitInfo getSplitInfo(int child) {
		
		return mapSplit.get(child);
	}

	
	String formulateQuery() {
		
		String query = "";
		for(int i=0;i<mapSplit.size();i++)
			query+= (i + ":" + attribute + mapSplit.get(i).getComparator() +mapSplit.get(i).getSplitValue())+"\n";
		return query;
	}
	
	public String toString() {
		
		String v= "SPLIT : attribute=" +attribute +" Nodo: "+ super.toString()+  " Split Variance: " + getVariance()+ "\n" ;
		
		for(SplitInfo si : mapSplit) {
			v+= "\t"+si+"\n";
		}
		
		return v;
	}
	
	public int compareTo(SplitNode o) {
		if(this.splitVariance == o.getVariance()) {
			
			return 0;
		} else if (this.splitVariance > o.getVariance()) {
			
			return 1;
		} else {
			
			return -1;
		}
	}
}
