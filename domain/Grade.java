package com.example.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Grade {
	@Column(name = "coursecode", nullable = false)
	private String coursecode;
	
	@Id
	private String Gradetitle;
	
	@Column(name = "Gradedate", nullable = false)
	private String Gradedate;
	
	@Column(name = "Gradecontent", nullable = false)
	private String Gradecontent;
	
	public String getCoursecode() {
		return coursecode;
	}
	
	public void setCoursecode(String coursecode) {
		this.coursecode = coursecode;
	}
	
	public String getGradetitle() {
		return Gradetitle;
	}
	
	public void setGradetitle(String Gradetitle) {
		this.Gradetitle = Gradetitle;
	}
	
	public String getGradedate() {
		return Gradedate;
	}
	
	public void setGradedate (String Gradedate) {
		this.Gradedate = Gradedate;
	}
	
	public String getGradecontent() {
		return Gradecontent;
	}
	
	public void setMajor(String Gradecontent) {
		this.Gradecontent = Gradecontent;
	}
}
