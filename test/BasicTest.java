import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

	@Before
	public void setup() {
		Fixtures.deleteDatabase();
	}

	// 1 city, 2 district, 3 address, 3 contactinfo
	// jobSeeker: bob, steve
	// steve has 2 resumes (firstResume, secondResume), bob has a resume (bobResume)
	// employer: tom, has a job (jobTest1)
	// application: SteveToTom, BobToTom

	@Test
	public void fullTest() {
		// Logging
		System.out.println();
		System.out.println("Running fullTest");

		// Load
		Fixtures.loadModels("data.yml");

		// Count things
		assertEquals(2, JobSeeker.count());
		assertEquals(4, Resume.count());
		assertEquals(1, Employer.count());
		assertEquals(1, Job.count());

		// Try to connect as user
		User user = JobSeeker.connect("bob@gmail.com", "secret");
		assertNotNull(user);
		assertEquals(user.email, "bob@gmail.com");
		assertEquals(user.userType, "Job Seeker");
		
		// Try to connect as job seekers
		JobSeeker bob = JobSeeker.connect("bob@gmail.com", "secret");
		assertNotNull(bob);
		JobSeeker steve = JobSeeker.connect("steve@gmail.com", "jobs");
		assertNotNull(steve);
		assertNull(JobSeeker.connect("jeff@gmail.com", "badpassword"));
		assertNull(Employer.connect("bob@gmail.com", "secret"));

		// Find all of Steve's resumes
		List<Resume> steveResumes = Resume.find("owner.email", "steve@gmail.com").fetch();
		assertEquals(3, steveResumes.size());
		assertEquals(3, steve.resumes.size());


		// Find the most recent resume
		Resume frontResume = Resume.find("order by postedAt desc").first();
		assertNotNull(frontResume);
		assertEquals("My Third Resume", frontResume.name);

		// Post a new resume
		bob.addResume("My Fourth Resume");
		assertEquals(2, bob.resumes.size());
		assertEquals(5, Resume.count());

		/*
		// Delete steve's latest resume
		steveResumes.get(2).delete();
		steve.resumes.remove(0);
		assertEquals(2, steve.resumes.size());
		assertEquals(4, Resume.count());
		assertEquals(2, ContactInfo.count());

		
		// Delete a null resume
		Resume.deleteResume(null);
		assertEquals(0, steve.resumes.size());
		assertEquals(1, Resume.count());
		 */

		Employer tom = Employer.find("byEmail", "tom@gmail.com").first();
		assertNotNull(tom);
		assertEquals(tom.userType, "Employer");
		assertEquals(tom.contactInfo.address.address, "BK");
		assertEquals(tom.contactInfo.address.city.name, "Hanoi");
		assertEquals(tom.contactInfo.contactEmail, "tom@gmail.com");		
		assertEquals(tom.description, "We do things");


	}

	@Test
	public void jobSeekerTest() {
		JobSeeker tom = new JobSeeker("tom@gmail.com", "secret").save();
		assertNotNull(tom);
		tom.addResume("Tom's 1st resume");
		tom.addResume("Tom's 2nd resume");
		
		// Counting
		assertEquals(1, JobSeeker.count());
		assertEquals(2, Resume.count());
		assertEquals(2, tom.resumes.size());
		
		// Getting tom's resume from Resume table
		Resume tomResume = Resume.find("byOwner", tom).first();
		assertNotNull(tomResume);
		
		// Removing 1 resume of tom
		tom.removeResume(0);
		assertEquals(1, Resume.count());
		assertEquals(1, tom.resumes.size());
		
		// Deleting tom
		JobSeeker.deleteJobSeeker(tom);
		assertEquals(0, JobSeeker.count());
		assertEquals(0, Resume.count());
		
	}
	
	@Test
	public void employerTest() {
		Employer tom = new Employer("tom@gmail.com", "secret").save();
		assertNotNull(tom);
		tom.addJob("Tom's 1st job");
		tom.addJob("Tom's 2nd job");
		
		// Counting
		assertEquals(1, Employer.count());
		assertEquals(2, Job.count());
		assertEquals(2, tom.jobs.size());
		
		// Getting tom's job from Job table
		Job tomJob = Job.find("byOwner", tom).first();
		assertNotNull(tomJob);
		
		// Removing 1 job of tom
		tom.removeJob(0);
		assertEquals(1, Job.count());
		assertEquals(1, tom.jobs.size());
		
		// Deleting tom
		Employer.deleteEmployer(tom);
		assertEquals(0, Employer.count());
		assertEquals(0, Job.count());
		
	}

	@Test
	public void companySizeTest() {
		// Logging
		System.out.println();
		System.out.println("Running companySizeTest");

		// Load
		Fixtures.loadModels("data.yml");

		// CompanySize test
		assertEquals(1, CompanySize.count());
		CompanySize companySize = CompanySize.all().first();
		assertEquals("< 10", companySize.size);

	}

	@Test
	public void cityTest() {
		// Logging
		System.out.println();
		System.out.println("Running cityTest");

		// City test
		// Create a new city
		City hanoi = new City("Hanoi").save();

		// Add a first district
		hanoi.addDistrict("Dong Da");
		hanoi.addDistrict("Hai Ba Trung");

		// Retrieve all districts (from District table)
		List<District> hanoiDistricts = District.find("byCity", hanoi).fetch();

		// Tests
		assertEquals(2, hanoiDistricts.size());

		District firstDistrict = hanoiDistricts.get(0);
		assertNotNull(firstDistrict);
		assertEquals("Dong Da", firstDistrict.name);

		District secondDistrict = hanoiDistricts.get(1);
		assertNotNull(secondDistrict);
		assertEquals("Hai Ba Trung", secondDistrict.name);



		// Retrieve Hanoi's districts (directly from hanoi entity)
		hanoiDistricts = null;
		firstDistrict = null;
		secondDistrict = null;

		hanoiDistricts = hanoi.districts; 

		// Tests
		assertEquals(2, hanoiDistricts.size());

		firstDistrict = hanoiDistricts.get(0);
		assertNotNull(firstDistrict);
		assertEquals("Dong Da", firstDistrict.name);

		secondDistrict = hanoiDistricts.get(1);
		assertNotNull(secondDistrict);
		assertEquals("Hai Ba Trung", secondDistrict.name);


		// Removing a Dong Da from Hanoi
		hanoi.removeDistrict(firstDistrict);
		assertEquals(1, hanoi.districts.size());
		assertEquals(1, District.count());
		assertEquals("Hai Ba Trung", hanoi.districts.get(0).name);

		hanoi.removeDistrict(0);
		assertEquals(0, hanoi.districts.size());
		assertEquals(0, District.count());
		
		
		City haiphong = new City("Hai Phong").save();
		haiphong.addDistrict("Hai An");
		assertEquals(2, City.count());
		assertEquals(1, District.count());
		City.deleteCity(haiphong);
		assertEquals(1, City.count());
		assertEquals(0, District.count());
	}

	@Test
	public void cityTestWithYml() {

		// Logging
		System.out.println();
		System.out.println("Running cityTestWithYml");

		Fixtures.loadModels("data.yml");
		List<District> hanoiDistricts = District.find("byName", "Dong Da").fetch();
		District d = hanoiDistricts.get(0);
		assertEquals("Hanoi", d.city.name);

		// Find City Hanoi
		City hanoi = City.find("byName", "Hanoi").first();
		assertNotNull(hanoi);
		assertEquals(2, hanoi.districts.size());
		assertEquals(2, District.count());

		// Remove a district from Hanoi
		District.deleteDistrict(hanoi.districts.get(0));
		assertEquals(2, hanoi.districts.size());
		assertEquals(2, District.count());
	}

	@Test
	public void addressTestWithYml() {
		// Logging
		System.out.println();
		System.out.println("Running addressTestWithYml");

		Fixtures.loadModels("data.yml");
		List<Address> address = Address.findAll();
		assertEquals(address.size(), 3);
		Address firstAddress = address.get(1);
		assertEquals("TM", firstAddress.address);
		assertEquals("Hanoi", firstAddress.city.name);
		assertEquals("Hai Ba Trung", firstAddress.district.name);
		
		// Deleting while the address is linked with its contactInfo
		ContactInfo contactInfo = firstAddress.contactInfo;
		assertEquals(true, Address.deleteAddress(firstAddress));
		assertEquals(Address.count(), 2);
		assertNull(contactInfo.address);
		
		// Deleting null address
		assertEquals(false, Address.deleteAddress(null));
	}
	
	@Test
	public void contactInfoTest() {
		// Logging
		System.out.println();
		System.out.println("Running contactInfoTest");
		
		ContactInfo contactInfo = new ContactInfo("dung@gmail.com").save();
		City hanoi = new City("Hanoi").save();
		contactInfo.address = new Address(contactInfo, hanoi).save();
		
		assertEquals(1, ContactInfo.count());
		assertEquals(1, City.count());
		assertEquals(1, Address.count());
		
		assertEquals(true, ContactInfo.deleteContactInfo(contactInfo));
		assertEquals(0, ContactInfo.count());
		assertEquals(1, City.count());
		assertEquals(0, Address.count());
	}
	
	@Test
	public void contactInfoTestWithYml() {
		// Logging
		System.out.println();
		System.out.println("Running contactInfoTestWithYml");
		
		Fixtures.loadModels("data.yml");
		
		// Counting
		assertEquals(3, ContactInfo.count());
		
		ContactInfo steveContactInfo = ContactInfo.find("byContactEmail", "steve@gmail.com").first();
		assertNotNull(steveContactInfo);
		
		JobSeeker steve = JobSeeker.find("byEmail", "steve@gmail.com").first();
		steveContactInfo = steve.contactInfo;
		assertNotNull(steveContactInfo);
		
		// Deleting steveContactInfo
		assertEquals(false, ContactInfo.deleteContactInfo(steveContactInfo));
		assertEquals(3, ContactInfo.count());
	}

	@Test
	public void jobTestWithYml() {
		// Logging
		System.out.println();
		System.out.println("Running jobTestWithYml");

		//Job test
		Fixtures.loadModels("data.yml");
		List <Job> job = Job.findAll();
		for (Job counter : job) {
			assertEquals("doSomething", counter.name);
			assertEquals("We do things", counter.owner.description);
		}		
		Job firstJob = job.get(0);
		assertEquals("doSomething", firstJob.name);
		assertEquals("We do things", firstJob.owner.description);
		assertEquals("tom@gmail.com", firstJob.contactInfo.contactEmail);

	}

	@Test
	public void majorTest() {
		// Logging
		System.out.println();
		System.out.println("Running majorTest");

		new Major("electronic").save();
		new Major("it").save();
		List <Major> major = Major.findAll();
		assertEquals(major.size(), 2);
	}

	@Test
	public void majorTestWithYml() {
		// Logging
		System.out.println();
		System.out.println("Running majorTestWithYml");

		Fixtures.loadModels("data.yml");
		List <Major> major = Major.findAll();
		assertEquals(major.size(), 2);
		Major firstMajor = major.get(0);
		assertEquals("computer science", firstMajor.name);
	}

	@Test 
	public void educationTestWithYml() {
		// Logging
		System.out.println();
		System.out.println("Running educationTestWithYml");

		// Loading
		Fixtures.loadModels("data.yml");
		
		List <Education> education = Education.findAll();
		assertEquals(education.size(), 1);
		Education firstEducation = education.get(0);
		assertEquals("BK", firstEducation.college);
		assertEquals(3.4, firstEducation.gpa, 0.1);
		assertEquals("electronic", firstEducation.major.name);
		
		Job job = Job.find("byRequiredEducation", firstEducation).first();
		assertNotNull(job);
		assertEquals("tom@gmail.com", job.owner.email);
	}

	@Test
	public void applicationTestWithYml() {
		// Logging
		System.out.println();
		System.out.println("Running applicationTestWithYml");

		// Loading
		Fixtures.loadModels("dungnguyendata.yml");
		
		// Counting
		assertEquals(1, JobSeeker.count());
		assertEquals(1, Employer.count());
		assertEquals(1, Application.count());
		assertEquals(1, Resume.count());
		assertEquals(1, Job.count());		
		
		// Getting the application
		Application application = Application.all().first();
		assertNotNull(application);
		assertEquals(application.resume.name, "Steve's 1st resume");
		assertEquals(application.job.name, "Tom's 1st job");
		assertEquals(application.jobSeeker.email, "steve@gmail.com");
		assertEquals(application.employer.email, "tom@gmail.com");
		assertEquals(1, application.jobSeeker.applications.size());
		assertEquals(1, application.employer.applications.size());
		
		// Deleting the application's resume
		assertEquals(false, Resume.deleteResume(application.resume));
		assertEquals(false, Job.deleteJob(application.job));
		assertEquals(false, JobSeeker.deleteJobSeeker(application.jobSeeker));
		assertEquals(false, Employer.deleteEmployer(application.employer));
	}
	
	@Test
	public void languageTestWithYml() {
		// Logging
		System.out.println();
		System.out.println("Running languageTestWithYml");
		
		// Loading
		Fixtures.loadModels("data.yml");
		
		// Counting
		assertEquals(2, Language.count());
		
		// Getting english
		Language english = Language.find("byLanguage", "English").first();
		assertNotNull(english);
		assertEquals(0, english.useCount);
		
		Resume resume = Resume.all().first();
		resume.addLanguage(english);
		assertEquals(1, english.useCount);
		assertEquals(1, resume.languages.size());
		
		// Deleting english
		assertEquals(false, Language.deleteLanguage(english));
		
		resume.removeLanguage(english);
		assertEquals(0, english.useCount);
		assertEquals(0, resume.languages.size());
		
		// Deleting english again
		assertEquals(true, Language.deleteLanguage(english));
		assertEquals(1, Language.count());
	}
}
