package org.launchcode.blogz.controllers;

import javax.servlet.http.HttpServletRequest;

import org.launchcode.blogz.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AuthenticationController extends AbstractController {
	
	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signupForm() {
		return "signup";
	}
	
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String signup(HttpServletRequest request, Model model) {
		
		// Get form fields.
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String verify   = request.getParameter("verify");
		
		// Indicates whether any errors get flagged.
		boolean flag = false;
		
		if (username == null || username == "") {
			model.addAttribute("username_error", "Must enter a username.");
			flag = true;
		} else {
			if (!User.isValidUsername(username)) {
				model.addAttribute("username_error", "Invalid username.");
				flag = true;
			}
			
			if (userDao.findByUsername(username) != null) {
				model.addAttribute("username_error", "Username already in use.");
				flag = true;
			}
		}
		
		if (password == null || password == "") {
			model.addAttribute("password_error", "Must enter a password.");
			flag = true;
		} else {
			if (!User.isValidPassword(password)) {
				model.addAttribute("password_error", "Invalid password.");
				flag = true;
			}
		}
		
		if (verify == null || verify == "") {
			model.addAttribute("verify_error", "Must enter a password verification.");
			flag = true;
		} else {
			if (!password.equals(verify)) {
				model.addAttribute("verify_error", "Verification does not match password.");
				flag = true;
			}
		}
		
		if (flag) {
			return "signup";
		}
		
		User user = new User(username, password);
		userDao.save(user);
		
		setUserInSession(request.getSession(), user);
		
		return "redirect:blog/newpost";
		
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginForm() {
		return "login";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(HttpServletRequest request, Model model) {
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		User user = userDao.findByUsername(username);
		
		boolean flag = false;
		
		if (username == null || username == "" || password == null || password == "") {
			model.addAttribute("error", "Must fill in both fields.");
			flag = true;
		} else {
			if (user == null) {
				model.addAttribute("error", "Unable to find user.");
				flag = true;
			} else {
				if (!user.isMatchingPassword(password)) {
					model.addAttribute("error", "Incorrect password.");
					flag = true;
				}
			}
		}
		
		if (flag) {
			return "login";
		}
		
		setUserInSession(request.getSession(), user);
		
		return "redirect:blog/newpost";
		
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request){
        request.getSession().invalidate();
		return "redirect:/";
	}
}
