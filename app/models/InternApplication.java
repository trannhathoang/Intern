package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.Model;
import play.data.validation.*;

@Entity
public class InternApplication extends Model {
	
	
	
	// Properties 
	
	// Required
	@Required
	@ManyToOne
	public InternJob job;

	// Required
	@Required
	@ManyToOne
	public InternResume resume;
	
	@ManyToOne
	public InternJobSeeker jobSeeker;
	
	@ManyToOne
	public InternEmployer employer;
	
	@Lob
	@MaxSize(10000)
	public String message;
	
	
	
	// Constructor
	
	public InternApplication(InternJob job, InternResume resume) {
		this.job = job;
		this.resume = resume;
		this.jobSeeker = resume.owner;
		this.employer = job.owner;
	}
	
	public InternApplication(InternJob job, InternResume resume, String message) {
		this (job, resume);
		this.message = message;
	}
	
	
	
	// Methods
	
	
	
	// Static methods
	
	public static boolean deleteApplication(InternApplication application) {
		if (application == null) {
			System.out.println("ERROR: Deleting null application");
			return false;
		}
		
		System.out.println("ERROR: All applications can never be deleted");		
		
		return false;
	}
}
