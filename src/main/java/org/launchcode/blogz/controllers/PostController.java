package org.launchcode.blogz.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.launchcode.blogz.models.Post;
import org.launchcode.blogz.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PostController extends AbstractController {

	@RequestMapping(value = "/blog/newpost", method = RequestMethod.GET)
	public String newPostForm() {
		return "newpost";
	}
	
	@RequestMapping(value = "/blog/newpost", method = RequestMethod.POST)
	public String newPost(HttpServletRequest request, Model model) {
		
		String title = request.getParameter("title");
		String body  = request.getParameter("body");
		
		if (title == null || title == "" || body == null || body == "") {
			model.addAttribute("error", "Fields may not be empty.");
			return "newpost";
		}
		
		User user = getUserFromSession(request.getSession());
		Post post = new Post(title, body, user);
		postDao.save(post);
		
		return String.format("redirect:/blog/%s/%s", user.getUsername(), post.getUid());
	}
	
	@RequestMapping(value = "/blog/{username}/{uid}", method = RequestMethod.GET)
	public String singlePost(@PathVariable String username, @PathVariable int uid, Model model) {
		
		User user = userDao.findByUsername(username);
		Post post = postDao.findByUid(uid);
		
		if (user == null || post == null || !(post.getAuthor().equals(user))) {
			return "notfound";
		}
		
		model.addAttribute("post", post);
		return "post";
	}
	
	@RequestMapping(value = "/blog/{username}", method = RequestMethod.GET)
	public String userPosts(@PathVariable String username, Model model) {
		
		User author = userDao.findByUsername(username);
		
		if (author == null) {
			return "notfound";
		}
		
		List<Post> posts = postDao.findByAuthor(author);
		
		model.addAttribute("posts", posts);
		
		return "blog";
	}
	
}
