package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.domain.Post;

public interface PostRepository extends JpaRepository<Post, String>{
	
}
