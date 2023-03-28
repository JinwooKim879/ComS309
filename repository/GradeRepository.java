package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.domain.Grade;

public interface GradeRepository extends JpaRepository<Grade, String>{
	
}