package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Classe utilizzata per modellare lo schema di una tabella di un database. A tale scopo
 * questa classe fa utilizzo della classe <code>Column</code> la quale modella, appunto, le colonne dello schema.<br>
 * <code>TableSchema</code> inoltre implementa l'interfaccia <code>Iterable</code> in modo da poter scorrerne
 * le colonne con un'istanza di <code>Iterator</code>
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
public class TableSchema implements Iterable<Column> {

	/**
	 * Lista contenente le informazioni sulle colonne della tabella
	 */
	private List<Column> tableSchema = new ArrayList<Column>();
	
	/**
	 * Costruttore di <code>TableSchema</code>.
	 * Facendo utilizzo di un oggetto di tipo <code>DbAccess</code> e una stringa contenente il nome
	 * di una tabella, ne ricava lo schema. Se la tabella non è presente, lo schema è restituito come vuoto.
	 * 
	 * @param db istanza di <code>DbAccess</code> che modella l'accesso ad un database
	 * @param tableName Stringa contenente il nome della tabella da cui ricavare lo schema.
	 * 
	 * @throws SQLException se ci sono errori durante la comunicazione con il database
	 */
	public TableSchema(DbAccess db, String tableName) throws SQLException {
		
		// Si mappano i tipi di SQL con "string" se possono essere resi in Java attraverso
		// una Stringa o con "number" se possono essere resi attraverso una variabile numerica
		HashMap<String, String> mapSQL_JAVATypes = new HashMap<String, String>();
	
		mapSQL_JAVATypes.put("CHAR", "string");
		mapSQL_JAVATypes.put("VARCHAR", "string");
		mapSQL_JAVATypes.put("LONGVARCHAR", "string");
		mapSQL_JAVATypes.put("BIT", "string");
		mapSQL_JAVATypes.put("SHORT", "number");
		mapSQL_JAVATypes.put("INT", "number");
		mapSQL_JAVATypes.put("LONG", "number");
		mapSQL_JAVATypes.put("FLOAT", "number");
		mapSQL_JAVATypes.put("DOUBLE", "number");
		
		
		// Si recupera la connessione da db, e da essa si ricava lo schema
		Connection con = db.getConnection();
		DatabaseMetaData meta = con.getMetaData();
		ResultSet res = meta.getColumns(null, null, tableName, null);
		   
		while (res.next()) {
			
			// per ogni colonna si crea un'istanza di Column con nome e tipo (number o string)
			if (mapSQL_JAVATypes.containsKey(res.getString("TYPE_NAME"))) {

				tableSchema.add(new Column(res.getString("COLUMN_NAME"),
							mapSQL_JAVATypes.get(res.getString("TYPE_NAME"))));
			}
		}
		
		// Infine si chiude il ResultSet
		res.close();  
	}

	/**
	 * Metodo getter per ottenere il numero di attributi dello schema.
	 * 
	 * @return Il numero di attributi dello schema. Se restituisce 0, allora la tabella non è stata trovata.
	 */
	public int getNumberOfAttributes(){

		return tableSchema.size();
	}

	/**
	 * Metodo getter per ottenere una specifica colonna dello schema.
	 * 
	 * @param index Intero non negativo che indica l'indice della colonna da recuperare (comincia da 0).
	 * 
	 * @return La colonne dello schema all'indice specificato.
	 */
	public Column getColumn(int index){

		return tableSchema.get(index);
	}

	/**
	 * Implementazione del metodo della interfaccia <code>Iterable</code> per ottenere l'iteratore per scorrere
	 * lo schema.
	 * 
	 * @return Un iteratore del tipo <code>Iterator\<Column\></code> per scorrere le colonne dello schema.
	 */
	public Iterator<Column> iterator() {

		return tableSchema.iterator();
	}
}
