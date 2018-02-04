package it.searchAny.db.connection;

import it.searchAny.db.dao.ContattiDAO;

//Abstract class DAO Factory
public abstract class DAOFactory {

// List of DAO types supported by the factory
public static final int ORACLE = 1;
public static final int MARIADB = 2;

// There will be a method for each DAO that can be 
// created. The concrete factories will have to 
// implement these methods.
public abstract ContattiDAO getContattiDAO();

public static DAOFactory getDAOFactory(
   int whichFactory) {

 switch (whichFactory) {
   case ORACLE    : 
       //return new OracleDAOFactory();      
   case MARIADB    : 
       return new MariaDBDAOFactory();
   default           : 
       return null;
 }
}
}
