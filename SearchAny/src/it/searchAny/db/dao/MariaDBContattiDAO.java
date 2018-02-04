package it.searchAny.db.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import it.searchAny.bean.Contatto;
import it.searchAny.db.connection.DAOFactory;
import it.searchAny.db.connection.MariaDBDAOFactory;

public class MariaDBContattiDAO implements ContattiDAO {

	private Connection conn = null;

	public MariaDBContattiDAO() {
		// initialization
	}

	// The following methods can use
	// CloudscapeDAOFactory.createConnection()
	// to get a connection as required

	public int insertContatto(Contatto contatto) {
		// Implement insert customer here.
		// Return newly created customer number
		// or a -1 on error
		int returnCode = 0;
		String query = "INSERT INTO CONTATTI (id_google,descrizione,citta,nazione,tipo,email,sito_web,email_inviata) values ('" + contatto.getIdGoogle() + "','" + contatto.getDescrizione() + "','"+contatto.getCitta() +"','"+
						contatto.getNazione()+"',"+"'"+contatto.getTipo()+"','"+ contatto.getEmail() + "','" + contatto.getSitoWeb() + "',"+"0)";
		System.out.println(query);
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(query);
			// stmt.setInt(1, id);
			return stmt.executeUpdate();
		} catch (SQLException e) {
			 e.printStackTrace();
			//throw new RuntimeException(e);
			 returnCode = -1;		 
		} finally {
			close(stmt);
			close(conn);
		}
		returnCode =1;
		return returnCode;
	}

	private Connection getConnection() {
		try {
			conn = ((MariaDBDAOFactory) DAOFactory.getDAOFactory(2)).createConnection();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return conn;
	}

	/**
	 * 
	 * 
	 * @param con
	 */
	private static void close(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// e.printStackTrace();
				//throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 
	 * 
	 * @param stmt
	 */
	private static void close(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				// e.printStackTrace();
				//throw new RuntimeException(e);
			}
		}
	}
	/*
	 * public boolean deleteCustomer() { // Implement delete customer here // Return
	 * true on success, false on failure return true; }
	 * 
	 * public Contatti findCustomer() { // Implement find a customer here using
	 * supplied // argument values as search criteria // Return a Transfer Object if
	 * found, // return null on error or if not found }
	 * 
	 * public boolean updateCustomer() { // implement update record here using data
	 * // from the customerData Transfer Object // Return true on success, false on
	 * failure or // error return true; }
	 * 
	 * public RowSet selectCustomersRS() { // implement search customers here using
	 * the // supplied criteria. // Return a RowSet. }
	 * 
	 * public Collection selectCustomersTO(...) { // implement search customers here
	 * using the // supplied criteria. // Alternatively, implement to return a
	 * Collection // of Transfer Objects. }
	 */

}
