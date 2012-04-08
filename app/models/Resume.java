package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Resume extends Model {
	
	
	
	// Properties
	
	public String name;
	public Date postedAt;
	public int workExperience;
	public Education education;
	public Address preferWorkLocation;
	
	@Lob
	public String description;
	
	@ManyToOne
	public JobSeeker owner;
	
	@OneToOne(cascade=CascadeType.ALL)
	public ContactInfo contactInfo;
	
	// ??????
	//public List<Application> applications;
	
	@ManyToMany
	public List<Language> languages;
	
	
	
	// Constructors
	
	public Resume(JobSeeker owner, String name) {
		this.owner = owner;
		this.name = name;
		this.postedAt = new Date();
	}
	
	public Resume(JobSeeker owner, String name, int workExperience, String description, Education education, Address prefer, ContactInfo contactInfo) {
		this(owner, name);
		this.workExperience = workExperience;
		this.description = description;
		this.education = education;
		this.preferWorkLocation = prefer;
		this.contactInfo = contactInfo;
	}
	
	
	
	// Method
	
	public String toString() {
		return this.name;
	}
	
	
	
	// Static method
	
	public static boolean deleteResume(Resume resume) {
		if (resume == null) {
			System.out.println("ERROR: Deleting null resume");
			return false;
		}
		
		resume.owner.resumes.remove(resume);
		resume.delete();
		
		return true;
	}
}
