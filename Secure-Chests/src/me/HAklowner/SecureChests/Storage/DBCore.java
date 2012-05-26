package me.HAklowner.SecureChests.Storage;

import java.sql.Connection;
import java.sql.ResultSet;

public interface DBCore {
	
	public Connection getConnection();
	
	public Boolean checkConnection();
	
	public void close();
	
	public ResultSet select(String query);
	
	public Boolean execute(String query);
	
	public Boolean tableExists(String query);

}
