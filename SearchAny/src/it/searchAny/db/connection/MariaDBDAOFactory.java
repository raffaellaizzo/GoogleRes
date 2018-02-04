package it.searchAny.db.connection;

import java.sql.Connection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import it.searchAny.db.dao.ContattiDAO;

public class MariaDBDAOFactory extends DAOFactory {

	// public static final String DRIVER_NAME= "Corg.mariadb.jdbc.Driver";
	// public static final String DBURL= "jdbc:mariadb://localhost:3306/delitaly";

	@Override
	public ContattiDAO getContattiDAO() {
		// TODO Auto-generated method stub
		return null;
	}

	public static Connection createConnection() {
		// Use DRIVER and DBURL to create a connection
		// Recommend connection pool implementation/usage

		try {
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:/comp/env/jdbc/JCGDelitalyDB");
			return ds.getConnection();
		} catch (Exception e) {
			// e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
