package edu.arizona.biosemantics.oto.oto.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.oto.oto.shared.model.Context;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class ContextDAO {

	private TermDAO termDAO;

	protected ContextDAO() {} 
	
	public void setTermDAO(TermDAO termDAO) {
		this.termDAO = termDAO;
	}

	public Context get(int id) throws ClassNotFoundException, SQLException, IOException {
		Context context = null;
		Query query = new Query("SELECT * FROM oto_context WHERE id = ?");
		query.setParameter(1, id);
		ResultSet result = query.execute();
		while(result.next()) {
			context = createContext(result);
		}
		query.close();
		return context;
	}
	
	public List<Context> get(Term term) throws ClassNotFoundException, SQLException, IOException {
		List<Context> contexts = new LinkedList<Context>();
		Query query = new Query("SELECT * FROM oto_context WHERE term = ?");
		query.setParameter(1, term.getId());
		ResultSet result = query.execute();
		while(result.next()) {
			Context context = createContext(result);
			contexts.add(context);
		}
		query.close();
		return contexts;
	}
	
	private Context createContext(ResultSet result) throws SQLException, ClassNotFoundException, IOException {
		int id = result.getInt(1);
		int termId = result.getInt(2);
		String source = result.getString(3);
		String sentence = result.getString(4);
		return new Context(id, termId, source, sentence);
	}

	public Context insert(Context context, int termId) throws ClassNotFoundException, SQLException, IOException {
		if(!context.hasId()) {
			Context result = null;
			Query insert = new Query("INSERT INTO `oto_context` " +
					"(`term`, `source`, `sentence`) VALUES (?, ?, ?)");
			insert.setParameter(1, termId);
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
		Query query = new Query("UPDATE oto_context SET term = ?, source = ?, sentence = ? WHERE id = ?");
		query.setParameter(1, context.getTermId());
		query.setParameter(2, context.getSource());
		query.setParameter(1, context.getSentence());
		query.executeAndClose();
	}
	
	public void remove(Context context) throws ClassNotFoundException, SQLException, IOException {
		Query query = new Query("DELETE FROM oto_context WHERE id = ?");
		query.setParameter(1, context.getId());
		query.executeAndClose();
	}	
	
}
