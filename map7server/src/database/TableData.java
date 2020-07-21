package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class TableData {

	private DbAccess db;
	

	
	public TableData(DbAccess db) {
		this.db=db;
	}

	public List<Example> getTransazioni(String table) throws SQLException, EmptySetException{
		LinkedList<Example> transSet = new LinkedList<Example>();
		Statement statement;
		TableSchema tSchema=new TableSchema(db,table);
		
		
		String query="select ";
		
		for(int i=0;i<tSchema.getNumberOfAttributes();i++) {
			
			Column c=tSchema.getColumn(i);
			
			if(i>0) {
				
				query+=",";
			}
			
			query += c.getColumnName();
		}
		if(tSchema.getNumberOfAttributes()==0)
			throw new SQLException();
		query += (" FROM "+table);
		
		statement = db.getConnection().createStatement();
		ResultSet rs = statement.executeQuery(query);
		boolean empty=true;
		while (rs.next()) {
			empty=false;
			Example currentTuple=new Example();
			for(int i=0;i<tSchema.getNumberOfAttributes();i++)
				if(tSchema.getColumn(i).isNumber())
					currentTuple.add(rs.getDouble(i+1));
				else
					currentTuple.add(rs.getString(i+1));
			transSet.add(currentTuple);
		}
		rs.close();
		statement.close();
		if(empty) throw new EmptySetException();
		
		
		return transSet;

	}

	public Set<Object> getDistinctColumnValues(String table, Column column) throws SQLException {
		
		TreeSet<Object> queryResult = new TreeSet<>();
		Statement s = db.getConnection().createStatement();
		boolean numFlag = column.isNumber();
		String colName = column.getColumnName();

		ResultSet r = s.executeQuery(
		"SELECT DISTINCT " + colName + " " +
		"FROM " + table + " " +
		"ORDER BY " + colName);
	
		if(numFlag) {
			while(r.next()) {
				queryResult.add(r.getDouble(colName));
			}
		} else {
			while(r.next()) {
				queryResult.add(r.getString(colName));
			}
		}
		r.close();
		s.close(); // Also closes ResultSet
	
		return queryResult;

	}
	
	public enum QUERY_TYPE {
		MIN, MAX
	}
}
