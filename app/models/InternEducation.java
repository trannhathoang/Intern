package models;

import javax.persistence.*;

import play.db.jpa.Model;
import play.data.validation.*;

@Entity
public class InternEducation extends Model {



	// Properties

	//Required
	@Required
	@Min(1)
	public int studyYears;

	@Min(0)
	public double gpa;
	
	public String college;

	@ManyToOne
	public InternMajor major;



	// Constructors

	public InternEducation(int studyYears) {
		this.studyYears = studyYears;
	}

	public InternEducation(int studyYears,
			String college, 
			double gpa, 
			InternMajor major) {
		this.studyYears = studyYears;
		this.college = college;
		this.gpa = gpa;
		this.major = major;
	}
	
	
	
	// Static methods
	
	public static boolean deleteEducation(InternEducation education) {
		if (education == null) {
			System.out.println("ERROR: Deleting null education");
			return false;
		}
		
		// Check if any Resume linking with this Education
		InternResume resume = InternResume.find("byEducation", education).first();
		if (resume != null) {
			resume.education = null;
			resume.save();
		}
		
		// Check if any Job linking with this Education
		InternJob job = InternJob.find("byRequiredEducation", education).first();
		if (job != null) {
			job.requiredEducation = null;
			job.save();
		}
		
		education.delete();
		return true;
		
	}
}