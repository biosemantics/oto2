package edu.arizona.biosemantics.oto.oto.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class LabelDAO {
	
	private LabelingDAO labelingDAO;
	private SynonymDAO synonymDAO;
	
	protected LabelDAO() {} 
	
	public void setLabelingDAO(LabelingDAO labelingDAO) {
		this.labelingDAO = labelingDAO;
	}
	
	public void setSynonymDAO(SynonymDAO synonymDAO) {
		this.synonymDAO = synonymDAO;
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
		Label label = new Label(id, collectionId, name, description);
		label.setMainTerms(labelingDAO.getMainTerms(label));
		label.setMainTermSynonymsMap(synonymDAO.get(label));
		return label;
	}

	public Label insert(Label label, int collectionId) throws SQLException, ClassNotFoundException, IOException {
		if(!label.hasId()) {
			Label result = null;
			Query insert = new Query("INSERT INTO `oto_label` " +
					"(`collection`, `name`, `description`) VALUES (?, ?, ?)");
			insert.setParameter(1, collectionId);
			insert.setParameter(2, label.getName());
			insert.setParameter(3, label.getDescription());
			insert.execute();
			ResultSet generatedKeys = insert.getGeneratedKeys();
			generatedKeys.next();
			int id = generatedKeys.getInt(1);
			insert.close();
			
			label.setId(id);
		}
		return label;
	}

	public void update(Label label) throws SQLException, ClassNotFoundException, IOException {
		Query query = new Query("UPDATE oto_label SET name = ?, description = ? WHERE id = ?");
		query.setParameter(1, label.getName());
		query.setParameter(2, label.getDescription());
		query.setParameter(3, label.getId());
		query.executeAndClose();
	}

	public void remove(Label label) throws SQLException, ClassNotFoundException, IOException {
		Query query = new Query("DELETE FROM oto_label WHERE id = ?");
		query.setParameter(1, label.getId());
		query.executeAndClose();
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

	public void ensure(Collection collection) throws ClassNotFoundException, SQLException, IOException {
		String ids = "";
		for(Label label : collection.getLabels()) {
			if(!label.hasId()) {
				Label newLabel = insert(label, collection.getId());
				label.setId(newLabel.getId());
				ids += newLabel.getId() + ",";
			}
			else {
				ids += label.getId() + ",";
				update(label);
			}
		}
		ids = (ids.isEmpty() ? ids : ids.substring(0, ids.length() - 1));
		
		String removeOldLabelsQuery = ids.isEmpty() ? "DELETE FROM oto_label WHERE collection = ?" : 
				"DELETE FROM oto_label WHERE collection = ? AND id NOT IN (" + ids + ")";
		Query removeOldLabels = new Query(removeOldLabelsQuery);
		removeOldLabels.setParameter(1, collection.getId());
		removeOldLabels.executeAndClose();
		
		for(Label label : collection.getLabels()) {
			labelingDAO.ensure(label, label.getMainTerms());
			
			for(Term mainTerm : label.getMainTerms()) {
				List<Term> synonymTerms = label.getSynonyms(mainTerm);
				synonymDAO.ensure(label, mainTerm, synonymTerms);
			}
		}
	}
}

