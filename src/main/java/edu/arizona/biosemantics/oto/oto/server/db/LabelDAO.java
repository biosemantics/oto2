package edu.arizona.biosemantics.oto.oto.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.arizona.biosemantics.oto.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class LabelDAO {
	
	private TermDAO termDAO;
	
	
	
	public void setTermDAO(TermDAO termDAO) {
		this.termDAO = termDAO;
	}

	public Label get(int id) throws SQLException, ClassNotFoundException, IOException {
		Label label = null;
		Query query = new Query("SELECT * FROM oto_label WHERE id = ?");
		query.setParameter(1, id);
		ResultSet result = query.execute();
		while(result.next()) {
			label = createLabel(result);
		}
		query.close();
		return label;
	}
	
	private Label createLabel(ResultSet result) throws SQLException, ClassNotFoundException, IOException {
		int id = result.getInt(1);
		int collectionId = result.getInt(2);
		String name = result.getString(3);
		String description = result.getString(4);
		Label label = new Label(id, name, description);
		label.setTerms(termDAO.getTerms(label));
		return label;
	}

	public Label insert(Label label) throws SQLException, ClassNotFoundException, IOException {
		if(!label.hasId()) {
			Label result = null;
			Query insert = new Query("INSERT INTO `oto_label` " +
					"(`collection`, `name`, `description`) VALUES (?, ?, ?)");
			insert.setParameter(1, label.getCollection().getId());
			insert.setParameter(2, label.getName());
			insert.setParameter(3, label.getDescription());
			insert.execute();
			ResultSet generatedKeys = insert.getGeneratedKeys();
			generatedKeys.next();
			int id = generatedKeys.getInt(1);
			insert.close();
			
			label.setId(id);
			
			for(Term term : label.getTerms())
				termDAO.insert(term);
		}
		return label;
	}

	public void update(Label label) throws SQLException, ClassNotFoundException, IOException {
		Query query = new Query("UPDATE oto_label SET name = ?, description = ? WHERE id = ?");
		query.setParameter(1, label.getName());
		query.setParameter(2, label.getDescription());
		query.setParameter(3, label.getId());
		
		Label oldLabel = this.get(label.getId());
		for(Term term : oldLabel.getTerms()) {
			termDAO.remove(term);
		}
		for(Term term : label.getTerms()) {
			termDAO.insert(term);
		}
		query.executeAndClose();
	}

	public void remove(Label label) throws SQLException, ClassNotFoundException, IOException {
		Query query = new Query("DELETE FROM oto_label WHERE id = ?");
		query.setParameter(1, label.getId());
		query.executeAndClose();
		
		for(Term term :  label.getTerms())
			termDAO.remove(term);
	}

	public List<Label> getLabels(Collection collection) throws SQLException, ClassNotFoundException, IOException {
		List<Label> labels = new LinkedList<Label>();
		Query query = new Query("SELECT * FROM oto_label WHERE collection = ?");
		query.setParameter(1, collection.getId());
		ResultSet result = query.execute();
		while(result.next()) {
			int id = result.getInt(1);
			labels.add(get(id));
		}
		query.close();
		return labels;		
	}

	public Set<Label> get(Term term) throws ClassNotFoundException, SQLException, IOException {
		Set<Label> labels = new HashSet<Label>();
		Query query = new Query("SELECT * FROM oto_labeling WHERE term = ?");
		query.setParameter(1, term.getId());
		ResultSet result = query.execute();
		while(result.next()) {
			labels.add(createLabel(result));
		}
		query.close();
		return labels;
	}

	public void insert(Term term, Label label) throws ClassNotFoundException, SQLException, IOException {
		Query query = new Query("INSERT INTO `oto_labeling` " +
				"(`term`, `label`) VALUES (?, ?)");
		query.setParameter(1, term.getId());
		query.setParameter(2, label.getId());
		ResultSet result = query.execute();
		query.close();
	}
}

