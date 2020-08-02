package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Classe che modella l’insieme di transazioni da un database e collezionate in una tabella.<br>
 * Da essa è possibile ottenere una lista di tuple per poter manipolarle, nonchè ottenere tutti i valori
 * distinti all'interno di una colonna.
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */

public class TableData {

	/**
	 * Riferimento all'accesso ad un database da cui trarre la tabella.
	 */
	private DbAccess db;
	

	/**
	 * Costruttore di <code>TableData</code>.
	 * Necessita di un'istanza di <code>DbAccess</code> già inizializzata al fine di 
	 * poter accedere ad un database e ricavarne una tabella.
	 * 
	 * @param db riferimento all'accesso al database da cui trarre la tabella.
	 */
	public TableData(DbAccess db) {
		
		this.db = db;
	}

	/**
	 * Metodo che ricava dalla tabella il cui nome è passato in input una lista 
	 * che contiene tutte le tuple della tabella sotto forma di istanze di <code>Example</code>.
	 * 
	 * @see Example
	 * 
	 * @param table Stringa contenente il nome della tabella da cui leggere i contenuti.
	 * 
	 * @return Lista di <code>Example</code> che modellano le tuple contenute all'interno della tabella.
	 * 
	 * @throws SQLException In caso di errore nell'esecuzione della query.
	 * @throws EmptySetException In caso la tabella sia vuota.
	 */
	public List<Example> getTransazioni(String table) throws SQLException, EmptySetException {
		
		LinkedList<Example> transSet = new LinkedList<Example>();
		Statement statement;
		// Si ricava lo schema della tabella
		TableSchema tSchema = new TableSchema(db, table);
		
		String query = "select ";
		
		for (int i = 0; i < tSchema.getNumberOfAttributes(); i++) {
			
			// Per ogni colonna dello schema si aggiunge il nome della colonna alla query
			Column c = tSchema.getColumn(i);
			
			if (i > 0) {

				query += ",";
			}
			
			query += c.getColumnName();
		}
		
		if (tSchema.getNumberOfAttributes() == 0) {
			
			// Lancia una SQLException se la tabella non esiste (lo schema non ha colonne)
			throw new SQLException();			
		}
		
		// Si esegue la query indicata dalla omonima stringa e se ne ricava il resultSet
		query += (" FROM " + table);
		statement = db.getConnection().createStatement();
		ResultSet rs = statement.executeQuery(query);
		boolean empty = true;

		while (rs.next()) {
			
			empty = false;
			Example currentTuple = new Example();
			
			// Per ogni tupla viene creata una istanza Example...
			for (int i = 0; i < tSchema.getNumberOfAttributes(); i++) {
				
				if (tSchema.getColumn(i).isNumber()) {
					
					currentTuple.add(rs.getDouble(i + 1));
				} else {
					
					currentTuple.add(rs.getString(i + 1));
				}
			}
			
			// ... e la aggiunge alla lista
			transSet.add(currentTuple);
		}
		
		// Si chiude il result set e il relativo statement
		rs.close();
		statement.close();
		
		if (empty) {
			// Se il result set era vuoto, viene lanciata una ESException.
			throw new EmptySetException();
		}
		
		
		return transSet;

	}

	/**
	 * Metodo che restituisce l'insieme dei valori distinti che possono essere rinvenuti in una singola
	 * colonna di una tabella di un database. Il risultato viene ottenuto eseguendo una <i>query</i> al database
	 * per ottenere i valori distinti di quella specifica tabella.
	 * 
	 * @param table Stringa contenente il nome della tabella da cui ricavare i valori
	 * @param column istanza di Column contenente il nome della colonna della tabella da 
	 * 		  cui ricavare i valori distinti
	 * @see Column
	 * 
	 * 
	 * @return un <code>Set</code> di <code>Object</code> contenente i valori distinti della colonna column nella tabella table.
	 * 		   Il <code>Set</code> ha i propri elementi ordinati in ordine ascendente.  
	 * 
	 * @throws SQLException Se avvene un errore nell'esecuzione della <i>query</i>.
	 */
	public Set<Object> getDistinctColumnValues(String table, Column column) throws SQLException {
		
		TreeSet<Object> queryResult = new TreeSet<>();
		// Al fine di ottenere un set ordinato, si fa uso di un TreeSet.
		
		Statement s = db.getConnection().createStatement();
		boolean numFlag = column.isNumber();
		String colName = column.getColumnName();

		// Si esegue la query per ottenere tutti gli elementi
		ResultSet r = s.executeQuery(
		"SELECT DISTINCT " + colName + " " +
		"FROM " + table);
	
		if (numFlag) { // Si controlla se la colonna è numerica e si leggono i valori di conseguenza 
			
			while (r.next()) {
				
				queryResult.add(r.getDouble(colName));
			}
		} else {
			
			while (r.next()) {
				
				queryResult.add(r.getString(colName));
			}
		}
		
		// Chiude Statement e ResultSet una volta terminata la lettura
		r.close();
		s.close();
	
		return queryResult;

	}
	
	public Set<Object> getDistinctColumnValues2(String table, Column column) throws SQLException {
		
		TreeSet<Object> queryResult = new TreeSet<>();
		// Al fine di ottenere un set ordinato, si fa uso di un TreeSet.
		
		Statement s = db.getConnection().createStatement();
		boolean numFlag = column.isNumber();
		String colName = column.getColumnName();

		// Si esegue la query per ottenere tutti gli elementi
		ResultSet r = s.executeQuery(
		"SELECT DISTINCT " + colName + " " +
		"FROM " + table);
	
		if (numFlag) { // Si controlla se la colonna è numerica e si leggono i valori di conseguenza 
			
			while (r.next()) {
				
				queryResult.add(r.getDouble(colName));
			}
		} else {
			
			while (r.next()) {
				
				queryResult.add(r.getString(colName));
			}
		}
		
		// Chiude Statement e ResultSet una volta terminata la lettura
		r.close();
		s.close();
	
		return queryResult;

	}
	
	/**
	 * Tipo enumerativo per distinguere il tipo di <i>query</i>.
	 */
	public enum QUERY_TYPE {
		MIN, MAX
	}
}
