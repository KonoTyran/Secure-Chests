package me.HAklowner.SecureChests.Storage;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.logging.Logger;

import me.HAklowner.SecureChests.SecureChests;

 /**
  * 
  * @author Brandon
  * Heavily influenced by phaed's Simple Clans SQLite manager
  * Thanks for the help ;)
  *
  */

public class SQLite implements DBCore{

	private Logger log;
	private Connection connection;
	private String dbLocation;
	private String dbName;
	private File file;

	public SQLite(String dbLocation) {
		this.dbName = "SCStorage";
		this.dbLocation = dbLocation;
		this.log = SecureChests.getLog();

		initialize();
	}

	private void initialize() {

		if (file == null)
		{
			File dbFolder = new File(dbLocation);

			if (dbName.contains("/") || dbName.contains("\\") || dbName.endsWith(".db"))
			{
				log.severe("The database name can not contain: /, \\, or .db");
				return;
			}
			if (!dbFolder.exists())
			{
				dbFolder.mkdir();
			}

			file = new File(dbFolder.getAbsolutePath() + File.separator + dbName + ".db");
		}
		try
		{
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
		}
		catch (SQLException ex)
		{
			log.severe("SQLite exception on initialize " + ex);
		}
		catch (ClassNotFoundException ex)
		{
			log.severe("You need the SQLite library " + ex);
		}
	}
	
	@Override
    public Connection getConnection()
    {
        if (connection == null)
        {
            initialize();
        }

        return connection;
    }
	
	@Override
    public Boolean checkConnection()
    {
        return getConnection() != null;
    }

    public void close()
    {
        try
        {
            if (connection != null)
            {
                connection.close();
            }
        }
        catch (Exception e)
        {
            log.severe("Failed to close database connection! " + e.getMessage());
        }
    }

    @Override
    public Boolean execute(String query)
    {
        try
        {
            getConnection().createStatement().execute(query);
            return true;
        }
        catch (SQLException ex)
        {
            log.severe(ex.getMessage());
            log.severe("Query: " + query);
            return false;
        }
    }
    
    @Override
    public Boolean tableExists(String table)
    {
        try
        {
            ResultSet tables = getConnection().getMetaData().getTables(null, null, table, null);
            return tables.next();
        }
        catch (SQLException ex)
        {
            log.severe("Failed to check if table '" + table + "' exists: " + ex.getMessage());
            return false;
        }
    }
    
    @Override
    public ResultSet select(String query)
    {
        try
        {

            return getConnection().createStatement().executeQuery(query);
        }
        catch (SQLException ex)
        {
            log.severe("Error at SQL Query: " + ex.getMessage());
            log.severe("Query: " + query);
        }
        return null;
    }
    
    public Boolean existsColumn(String tabell, String colum)
    {
        try
        {
            ResultSet colums = getConnection().getMetaData().getColumns(null, null, tabell, colum);
            return colums.next();
        }
        catch (SQLException e)
        {
           SecureChests.getLog().severe("Failed to check if column '" + colum + "' exists: " + e.getMessage());
            return false;
        }
    }

	@Override
	public ResultSet select(PreparedStatement query)
	{
		try
		{
			return query.executeQuery();
		}
		catch (SQLException e)
		{
            log.severe("Error at SQL Query: " + e.getMessage());
            log.severe("Query: " + query);
		}
		return null;
	}

	@Override
	public PreparedStatement PrepareStatement(String query)
	{
		try
		{
			return getConnection().prepareStatement(query);
		}
		catch (SQLException e)
		{
			return null;
		}
	}

}