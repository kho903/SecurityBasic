package com.jikim.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jikim.security1.config.auth.PrincipalDetails;
import com.jikim.security1.model.User;
import com.jikim.security1.repository.UserRepository;

@Controller // view를 리턴하겠다.
public class IndexController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/test/login")
	public @ResponseBody String testLogin(
		Authentication authentication,
		// @AuthenticationPrincipal UserDetails userDetails
		@AuthenticationPrincipal PrincipalDetails userDetails
	) { // DI(의존성 주입)
		System.out.println("/test/login ==================");
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		System.out.println("authentication = " + principalDetails.getUser());

		System.out.println("userDetails = " + userDetails.getUsername());
		return "세션 정보 확인하기";
	}

	@GetMapping("/test/oauth/login")
	public @ResponseBody String testOAuthLogin(
		Authentication authentication,
		@AuthenticationPrincipal OAuth2User oAuth
	) { // DI(의존성 주입)
		System.out.println("/test/login ==================");
		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		System.out.println("authentication = " + oAuth2User.getAttributes());
		System.out.println("oAuth2User.getAttributes() = " + oAuth2User.getAttributes());
		return "OAuth 세션 정보 확인하기";
	}

	// localhost:8080/
	// localhost:8080
	@GetMapping({"", "/"})
	public String index() {
		// 머스테치 기본 폴더 src/main/resources/
		// 뷰 리졸버 설정 : templates (prefix) .mustache (suffix) 생략 가능!
		return "index"; // src/main/resources/templates/index.mustache
	}

	// OAuth 로그인을 해도		PrincipalDetails
	// 일반 로그인을 해도		PrincipalDetails
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("principalDetails.getUser() = " + principalDetails.getUser());
		return "user";
	}

	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}

	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}

	// 스프링 시큐리티가 해당 주소를 낚아챔! -> SecurityConfig 파일에서 작동하지 않게 함.
	@GetMapping("/loginForm")
	public String login() {
		return "loginForm";
	}

	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}

	@PostMapping("/join")
	public String join(User user) {
		System.out.println();
		user.setRole("ROLE_USER");
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		userRepository.save(user);
		return "redirect:/loginForm";
	}

	@Secured("ROLE_ADMIN")
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인정보";
	}

	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "데이터정보";
	}
}
