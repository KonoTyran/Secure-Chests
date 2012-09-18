package me.HAklowner.SecureChests.Storage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public interface DBCore {
	
	public Connection getConnection();
	
	public Boolean checkConnection();
	
	public void close();
	
	public ResultSet select(String query);
	
	public ResultSet select(PreparedStatement query);
	
	public Boolean execute(String query);
	
	public Boolean tableExists(String query);

	public Boolean existsColumn(String tabell, String colum);

	public PreparedStatement PrepareStatement(String query);

}
