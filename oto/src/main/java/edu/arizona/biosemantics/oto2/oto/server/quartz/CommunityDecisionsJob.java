package edu.arizona.biosemantics.oto2.oto.server.quartz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Set;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.oto.server.Configuration;
import edu.arizona.biosemantics.oto2.oto.server.db.DAOManager;
import edu.arizona.biosemantics.oto2.oto.shared.model.community.Categorization;
import edu.arizona.biosemantics.oto2.oto.shared.model.community.Synonymization;

public class CommunityDecisionsJob implements Job {

	private DAOManager daoManager = new DAOManager();
	
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		log(LogLevel.INFO, "Start Community Decisions Job");
		
		saveCommunityDecisions();
		    
		log(LogLevel.INFO, "Completed Community Decisions Job");
	}

	public void saveCommunityDecisions() {
		Set<String> types = daoManager.getCollectionDAO().getTypes();
		for(String type : types) {
			log(LogLevel.INFO, "Create community decisions for type " + type);
			Set<Categorization> categorizations = daoManager.getCommunityDAO().getCategorizations(type);
			Set<Synonymization> synonymizations = daoManager.getCommunityDAO().getSynoymizations(type);
			    
			try {
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(Configuration.files + File.separator + "CommunityDecisions." + type + ".Categorization.ser"));
				out.writeObject(categorizations);
			    out.close();
			    
			    out = new ObjectOutputStream(new FileOutputStream(Configuration.files + File.separator + "CommunityDecisions." + type + ".Synonymization.ser"));
				out.writeObject(synonymizations);
			    out.close();
			} catch(Exception e) {
				log(LogLevel.ERROR, "Couldn't write community decisions", e);
			}
		}
	}

}