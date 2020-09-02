package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Classe che modella l’insieme di transazioni da un database, collezionate in una tabella.<br>
 * Da essa e'possibile ottenere una lista di tuple per poter manipolarle, nonche' ottenere tutti i valori
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
	 * @param db Riferimento all'accesso al database da cui trarre la tabella.
	 */
	public TableData(DbAccess db) {
		
		this.db = db;
	}

	/**
	 * Metodo che ricava dalla tabella il cui nome è passato in input, una lista 
	 * che contiene tutte le tuple della tabella sotto forma di istanze di <code>Example</code>.
	 * 
	 * @param table Stringa contenente il nome della tabella da cui leggere i contenuti.
	 * 
	 * @return Lista di <code>Example</code> che modellano le tuple contenute all'interno della tabella.
	 * 
	 * @throws SQLException In caso di errore nell'esecuzione della query.
	 * @throws EmptySetException In caso la tabella sia vuota.
	 * 
	 * @see Example
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
	 * @param column Istanza di Column contenente il nome della colonna della tabella da 
	 * 		  cui ricavare i valori distinti
	 * 
	 * 
	 * @return un <code>Set</code> di <code>Object</code> contenente i valori distinti assunti nell'attributo specificato nella tabella.
	 * 		   Il <code>Set</code> ha i propri elementi ordinati in ordine ascendente.  
	 * 
	 * @throws SQLException Se avviene un errore nell'esecuzione della <i>query</i>.
	 * @throws NullPointerException Se uno dei valori letti in una colonna a valori discreti e' <code>NULL</code>
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
	
		// Si controlla se la colonna e' numerica e si leggono i valori di conseguenza 
		if (numFlag) {

			
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
	 * Metodo per verificare che nella tabella non ci siano tuple con almeno un attributo nullo.
	 * Tale metodo e' fondamentale poiche' la presenza di valori "NULL" puo' generare errori in altri metodi
	 * o alterare i risultati delle operazioni eseguite sui valori nella tabella.
	 * 
	 * @param table Stringa contenente il nome della tabella in cui controllare la presenza di valori <code>NULL</code>
	 * 
	 * @return True se nella tabella table vi e' almeno una tupla con almeno un attributo avvalorato con <code>NULL</code>,
	 * 		   False altrimenti.
	 * 
	 * @throws SQLException Se la tabella non e' stata trovata o in caso di errori durante la connessione al database.
	 */
	public boolean hasNull(String table) throws SQLException {
		
		boolean result;
		TableSchema schema = new TableSchema(db, table);
		if (schema.getNumberOfAttributes() == 0) {
			
			// Lancia una SQLException se la tabella non esiste (lo schema non ha colonne)
			throw new SQLException();			
		}

		// Si costruisce la query in modo da contare le tuple con almeno un attributo nullo
		StringBuffer query = new StringBuffer("SELECT COUNT(*) FROM " + table + " WHERE " +
				schema.getColumn(0).getColumnName() + " IS NULL ");
		
		for (int i = 1; i < schema.getNumberOfAttributes(); i++) {
			
			query.append("OR " + schema.getColumn(i).getColumnName() + " IS NULL ");
		}
		
		query.append(";");
		
		Statement s = db.getConnection().createStatement();
		ResultSet r = s.executeQuery(query.toString());
		// Si sposta il cursore sul risultato prodotto dalla count
		r.next();
		// Il risultato e' ottenuto confrontando il risultato del conteggio con 0
		result = r.getInt("count(*)") != 0;
		
		// Chiude Statement e ResultSet una volta terminata la lettura
		r.close();
		s.close();
		return result;
	}
	
	/**
	 * Tipo enumerativo per distinguere il tipo di <i>query</i>.
	 */
	public enum QUERY_TYPE {
		MIN, MAX
	}
}
