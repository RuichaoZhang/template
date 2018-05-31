package com.neo.web;

import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neo.config.Const;
import com.neo.entity.User;
import com.neo.json.AjaxJson;
import com.neo.service.UserService;

import io.swagger.annotations.Api;
@Api(value = "UserController", description = "用户操作接口")
@Controller
public class UserController {

	@Resource
	UserService userService;

	@RequestMapping(value = "/logon", method = RequestMethod.POST)
	@ResponseBody
	public AjaxJson login(@RequestBody User reqUser, HttpServletRequest request, HttpServletResponse response) {
		AjaxJson j = new AjaxJson();
		String password = reqUser.getPassword();
		String userName = reqUser.getUserName();

		User user = userService.findByuserNameAndpassword(userName, password);
		if (null != user) {
			System.out.println(UUID.randomUUID().toString());
			user.setToken(UUID.randomUUID().toString());
			userService.save(user);
			user.setPassword("");
			j.setObject(user);
			j.setSuccess(Const.TRUE);
			Cookie cookie = new Cookie("token", user.getToken());
			cookie.setMaxAge(30 * 60);// 设置为30min
			cookie.setPath("/");
			response.addCookie(cookie);
			j.setMessage(Const.LOGIN_SUCCESS);
		} else {
			j.setSuccess(Const.FALSE);
			j.setMessage(Const.LOGIN_ERROR);
		}
		return j;
	}

	@RequestMapping("/delete")
	public String delete(Long id) {
		userService.delete(id);
		return "redirect:/list";
	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	@ResponseBody
	public AjaxJson logout(@RequestBody User reqUser, HttpServletRequest request, HttpServletResponse response) {
		System.out.println(reqUser);
		AjaxJson j = new AjaxJson();
		String token = reqUser.getToken();
		User user = userService.findUserByToken(token);
		user.setToken("");
		userService.save(user);
		j.setSuccess(Const.TRUE);
		j.setMessage(Const.LOGOUT_ERROR);
		response.addCookie(new Cookie("token", ""));
		return j;
	}

	@RequestMapping(value = "/checkUserExits", method = RequestMethod.GET)
	@ResponseBody
	public AjaxJson checkUserExits(HttpServletRequest request, HttpServletResponse response) {
		AjaxJson j = new AjaxJson();
		String userName = request.getParameter("userName");
		if(userService.checkUserExits(userName)) {
			j.setSuccess(false);
			j.setMessage(Const.REGISTER_ERROR);
			
		}else {
			j.setSuccess(true);
			j.setMessage(Const.REGISTER_SUCCESS);
		}
		return j;
	}
	
}
