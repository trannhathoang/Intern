package models;

import play.db.jpa.Model;
import java.util.*;
import javax.persistence.*;

@Entity
public class District extends Model {
	
	
	
	// Properties
	public String name;
	
	@ManyToOne
	public City city;
	
	
	
	// Constructors
	public District(City c, String n) {
		this.city = c;
		this.name = n;
	}
}