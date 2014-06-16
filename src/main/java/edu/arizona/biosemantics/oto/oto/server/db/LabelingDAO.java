package edu.arizona.biosemantics.oto.oto.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class LabelingDAO {

	private static LabelingDAO instance;
	
	public static LabelingDAO getInstance() {
		if(instance == null)
			instance = new LabelingDAO();
		return instance;
	}
	
	public Label getLabel(int id) throws SQLException, ClassNotFoundException, IOException {
		Label label = null;
		Query query = new Query("SELECT * FROM label WHERE id = ?");
		query.setParameter(1, id);
		ResultSet result = query.execute();
		while(result.next()) {
			id = result.getInt(1);
			int collection = result.getInt(2);
			String name = result.getString(3);
			String description = result.getString(4);
			label = new Label(id, CollectionDAO.getInstance().get(collection), name, description);
			label.setTerms(TermDAO.getInstance().getTerms(label));
		}
		query.close();
		return label;
	}
	
	public Label insert(Label label) throws SQLException, ClassNotFoundException, IOException {
		Label result = null;
		Query insert = new Query("INSERT INTO `label` " +
				"(`name`, `description`) VALUES (?, ?)");
		insert.setParameter(1, label.getName());
		insert.setParameter(2, label.getDescription());
		insert.execute();
		ResultSet generatedKeys = insert.getGeneratedKeys();
		generatedKeys.next();
		int id = generatedKeys.getInt(1);
		insert.close();
		
		for(Term term : label.getTerms())
			TermDAO.getInstance().insert(term);
		
		return getLabel(id);
	}

	public void update(Label label) throws SQLException, ClassNotFoundException, IOException {
		Query query = new Query("UPDATE label SET name = ?, description = ? WHERE id = ?");
		query.setParameter(1, label.getName());
		query.setParameter(2, label.getDescription());
		query.setParameter(3, label.getId());
		
		TermDAO termDAO = TermDAO.getInstance();
		Label oldLabel = this.getLabel(label.getId());
		for(Term term : oldLabel.getTerms()) {
			termDAO.remove(term);
		}
		for(Term term : label.getTerms()) {
			termDAO.insert(term);
		}
		query.executeAndClose();
	}

	public void remove(Label label) throws SQLException, ClassNotFoundException, IOException {
		Query query = new Query("DELETE FROM label WHERE id = ?");
		query.setParameter(1, label.getId());
		query.executeAndClose();
		
		for(Term term :  label.getTerms())
			TermDAO.getInstance().remove(term);
	}

	public List<Label> getLabels(Collection collection) throws SQLException, ClassNotFoundException, IOException {
		List<Label> labels = new LinkedList<Label>();
		Query query = new Query("SELECT * FROM label WHERE collection = ?");
		query.setParameter(1, collection.getId());
		ResultSet result = query.execute();
		while(result.next()) {
			int id = result.getInt(1);
			labels.add(LabelDAO.getInstance().getLabel(id));
		}
		query.close();
		return labels;		
	}
	
}
