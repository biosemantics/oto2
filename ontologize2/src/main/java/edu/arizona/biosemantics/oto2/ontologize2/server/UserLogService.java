package edu.arizona.biosemantics.oto2.ontologize2.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.oto2.ontologize2.shared.IUserLogService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;

/**
 * for recording user operation logs
 * @author maojin
 *
 */
public class UserLogService extends RemoteServiceServlet implements IUserLogService {
	public static String databaseHost;
	public static String databasePort;
	public static String databaseName;
	public static String databaseUser;
	public static String databasePassword;
	
	public UserLogService(){
		Properties properties = new Properties(); 
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			properties.load(loader.getResourceAsStream("edu/arizona/biosemantics/etcsite/config.properties"));
			databaseHost = properties.getProperty("databaseHost");
			databasePort = properties.getProperty("databasePort");
			databaseName = properties.getProperty("databaseName");
			databaseUser = properties.getProperty("databaseUser");
			databasePassword = properties.getProperty("databasePassword");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	public Connection getConnect() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			return DriverManager.getConnection("jdbc:mysql://"+databaseHost+":"+databasePort+"/"+databaseName, databaseUser,
					databasePassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void closeConnect(Connection conn){
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void createUserLogTable() {
		try(Connection connection = this.getConnect()) {
			try(Statement statement = connection.createStatement()) {
		        String query = "create table if not exists user_log (  `ID` bigint(20) NOT NULL auto_increment,  `userid` varchar(256) default NULL,"
		        		+ "  `sessionid` varchar(32) default NULL,  `operation` varchar(16) default NULL,  `term` varchar(256) default NULL,  `current_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,"
		        		+ "  PRIMARY KEY  (`ID`)) ENGINE=InnoDB DEFAULT CHARSET=utf8";
		        statement.execute(query);        
			}
			this.closeConnect(connection);
		}catch(SQLException e) {
			System.err.print(e);
		}
    }
	
	@Override
	public void insertLog(String userid, String sessionid, String ontology, String operation, String term){
		try(Connection connection = this.getConnect()) {
			try(PreparedStatement statement = connection.prepareStatement("insert into user_log (userid, sessionid, ontology, operation, superlevel) value (?,?,?,?,?)")) {
				statement.setString(1, userid);
				statement.setString(2, sessionid);
				statement.setString(3, ontology);
				statement.setString(4, operation);
				statement.setString(5, term);
				statement.executeUpdate();
			}
			this.closeConnect(connection);
		} catch(SQLException e) {
			System.err.print(e);
		}
	}
	
	@Override
	public void insertEdgeLog(String userid, String sessionid, String ontology, String operation, Edge edge) throws Exception{
		try(Connection connection = this.getConnect()) {
			try(PreparedStatement statement = connection.prepareStatement("insert into user_log (userid, sessionid, ontology, operation, superlevel, origin, targetlevel, relation) value (?,?,?,?,?,?,?,?)")) {
				statement.setString(1, userid);
				statement.setString(2, sessionid);
				statement.setString(3, ontology);
				statement.setString(4, operation);
				statement.setString(5, edge.getSrc().toString());
				statement.setString(6, edge.getOrigin().toString());
				statement.setString(7, edge.getDest().toString());
				statement.setString(8, edge.getType().name());
				statement.executeUpdate();
			}
			this.closeConnect(connection);
		} catch(SQLException e) {
			System.err.print(e);
		}
	}
	
    public static void main( String[] args )
    {
    	UserLogService userlogService = new UserLogService();
    	//userlogService.createUserLogTable();
    	userlogService.insertLog("1", "sessionid", "0", "enteroperation", "term");
    }

	
}
