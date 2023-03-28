package com.example.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Post {
	@Column(name = "coursecode", nullable = false)
	private String coursecode;
	
	@Id
	private String posttitle;
	
	@Column(name = "postdate", nullable = false)
	private String postdate;
	
	@Column(name = "postcontent", nullable = false)
	private String postcontent;
	
	public String getCoursecode() {
		return coursecode;
	}
	
	public void setCoursecode(String coursecode) {
		this.coursecode = coursecode;
	}
	
	public String getPosttitle() {
		return posttitle;
	}
	
	public void setPosttitle(String posttitle) {
		this.posttitle = posttitle;
	}
	
	public String getPostdate() {
		return postdate;
	}
	
	public void setPostdate (String postdate) {
		this.postdate = postdate;
	}
	
	public String getPostcontent() {
		return postcontent;
	}
	
	public void setMajor(String postcontent) {
		this.postcontent = postcontent;
	}
}
