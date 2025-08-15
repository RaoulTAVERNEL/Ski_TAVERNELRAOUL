package be.tav.pojo;

import be.tav.exception.BusinessException;

public abstract class Person {
	private int id;
	private String lastname;
	private String firstname;
	private String email;
	private String phone;
	
	public Person(int id, String ln, String fn, String e, String p) {
		this.id = id;
		setLastname(ln);
		setFirstname(fn);
		setEmail(e);
		setPhone(p);
	}
	
	public int getId() { return this.id; }
	
	public String getLastname() { return this.lastname; }
	
	public void setLastname(String ln) throws IllegalArgumentException {
		if(ln == null || ln.trim().isEmpty()) {
			throw new IllegalArgumentException("setLastname a échoué: argument null/empty.");
		}
		
		this.lastname = ln;
	}
	
	public String getFirstname() { return this.firstname; }
	
	public void setFirstname(String fn) throws IllegalArgumentException {
		if(fn == null || fn.trim().isEmpty()) {
			throw new IllegalArgumentException("setFirstname a échoué: argument null/empty.");
		}
		
		this.firstname = fn;
	}
	
	public String getEmail() { return this.email; }
	
	public void setEmail(String e) throws IllegalArgumentException, BusinessException {
	    if (e == null || e.trim().isEmpty()) {
	        throw new IllegalArgumentException("setEmail a échoué: argument null/empty.");
	    }

	    String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
	    if (!e.matches(emailRegex)) {
	        throw new BusinessException("L'email " + e + " n'est pas un format email valide.");
	    }

	    this.email = e;
	}

	
	public String getPhone() { return this.phone; }
	
	public void setPhone(String p) throws IllegalArgumentException {
		if(p == null || p.trim().isEmpty()) {
			throw new IllegalArgumentException("setPhone a échoué: argument null/empty.");
		}
		
		this.phone = p;
	}
}
