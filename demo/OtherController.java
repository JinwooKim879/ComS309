package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.Grade;
import com.example.domain.Post;
import com.example.domain.User;
import com.example.repository.PostRepository;
import com.example.repository.UserRepository;

@EnableJpaRepositories("com.example.repository")
@EntityScan("com.example.domain")
@RestController
public class OtherController {
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/user")
	public List<User> getUserList() {
		return userRepository.findAll();
	}
	
	@PostMapping("/user")
	public User getUserInsert(@RequestBody User user) {
		return userRepository.save(user);
	}
	
	@DeleteMapping("/user")
	public void getUserDelete(@RequestBody User user) {	
		userRepository.deleteById(user.getId());	
	}
	
	@Autowired
	private PostRepository postRepository;
	
	@GetMapping("/post")
	public List<Post> getPostList() {
		return postRepository.findAll();
	}
	
	@PostMapping("/post")
	public Post getPostInsert(@RequestBody Post post) {
		return postRepository.save(post);
	}
	
	@DeleteMapping("/post")
	public void getPostDelete(@RequestBody Post post) {	
		postRepository.deleteById(post.getPosttitle());	
	}
	

	
}
