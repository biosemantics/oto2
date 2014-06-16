package edu.arizona.biosemantics.oto.oto.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.arizona.biosemantics.oto.oto.shared.model.Context;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class ContextDAO {

	private static ContextDAO instance;
	
	public static ContextDAO getInstance() {
		if(instance == null)
			instance = new ContextDAO();
		return instance;
	}
	
	public Context get(int id) throws ClassNotFoundException, SQLException, IOException {
		Context context = null;
		Query query = new Query("SELECT * FROM context WHERE id = ?");
		query.setParameter(1, id);
		ResultSet result = query.execute();
		while(result.next()) {
			context = createContext(result);
		}
		query.close();
		return context;
	}
	
	private Context createContext(ResultSet result) throws SQLException, ClassNotFoundException, IOException {
		int id = result.getInt(1);
		int termId = result.getInt(2);
		String source = result.getString(3);
		String sentence = result.getString(4);
		return new Context(id, TermDAO.getInstance().get(termId), source, sentence);
	}

	public Context insert(Context context) throws ClassNotFoundException, SQLException, IOException {
		if(!context.hasId()) {
			Context result = null;
			Query insert = new Query("INSERT INTO `context` " +
					"(`term`, `source`, `sentence`) VALUES (?, ?, ?)");
			insert.setParameter(1, context.getTerm().getId());
			insert.setParameter(2, context.getSource());
			insert.setParameter(3, context.getSentence());
			insert.execute();
			ResultSet generatedKeys = insert.getGeneratedKeys();
			generatedKeys.next();
			int id = generatedKeys.getInt(1);
			insert.close();
			context.setId(id);
		}
		return context;
	}
	
	public void update(Context context) throws SQLException, ClassNotFoundException, IOException {
		Query query = new Query("UPDATE context SET term = ?, source = ?, sentence = ? WHERE id = ?");
		query.setParameter(1, context.getTerm().getId());
		query.setParameter(2, context.getSource());
		query.setParameter(1, context.getSentence());
		query.executeAndClose();
	}
	
	public void remove(Context context) throws ClassNotFoundException, SQLException, IOException {
		Query query = new Query("DELETE FROM context WHERE id = ?");
		query.setParameter(1, context.getId());
		query.executeAndClose();
	}	
	
}
