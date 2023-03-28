package com.example.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
	@Column(name = "name", nullable = false)
	private String name;
	
	@Id
	private String studentid;
	
	@Column(name = "classification", nullable = false)
	private String classification;
	
	@Column(name = "major", nullable = false)
	private String major;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return studentid;
	}
	
	public void setId(String id) {
		this.studentid = id;
	}
	
	public String getClassification() {
		return classification;
	}
	
	public void setClassification (String classification) {
		this.classification = classification;
	}
	
	public String getMajor() {
		return major;
	}
	
	public void setMajor(String major) {
		this.major = major;
	}
}
