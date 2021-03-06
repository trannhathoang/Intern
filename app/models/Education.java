package models;

import javax.persistence.*;

import play.db.jpa.Model;

@Entity
public class Education extends Model {



	// Properties

	//Required
	public int studyYears;

	public double gpa;
	public String college;

	@ManyToOne
	public Major major;



	// Constructors

	public Education(int studyYears) {
		this.studyYears = studyYears;
	}

	public Education(int studyYears,
			String college, 
			double gpa, 
			Major major) {
		this.studyYears = studyYears;
		this.college = college;
		this.gpa = gpa;
		this.major = major;
	}
	
	
	
	// Static methods
	
	public static boolean deleteEducation(Education education) {
		if (education == null) {
			System.out.println("ERROR: Deleting null education");
			return false;
		}
		
		// Check if any Resume linking with this Education
		Resume resume = Resume.find("byEducation", education).first();
		if (resume != null) {
			resume.education = null;
			resume.save();
		}
		
		// Check if any Job linking with this Education
		Job job = Job.find("byRequiredEducation", education).first();
		if (job != null) {
			job.requiredEducation = null;
			job.save();
		}
		
		education.delete();
		return true;
		
	}
}