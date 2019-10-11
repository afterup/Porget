package com.porget.control;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.porget.domain.UserVO;
import com.porget.persistence.PortfolioDAO;
import com.porget.persistence.UserDAO;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
public class MainController {
	
	@Autowired
	private PortfolioDAO dao;
	
	@Autowired
	private UserDAO userdao;
	
	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoder;

	@GetMapping(value= {"/",""})
	public String index() {
		return "common/index";
	}
	
	@GetMapping("/userjoin")//구직자 회원가입 폼
	public String insertUser() {
		return "member/userJoin";
	}
	
	@PostMapping("/userjoin")//회원가입 
    public String userJoin(MultipartFile file,UserVO vo,  
    				HttpServletRequest request) throws Exception {
    	vo.setUphoto("defaultMan.png");
        //비밀번호 암호화
        vo.setUpass(this.bcryptPasswordEncoder.encode(vo.getUpass()));
        userdao.insert(vo);
        userdao.insertAuth(vo.getUname());
        
		return "common/index";
	}
	
	@GetMapping("/login")//로그인창
	public String login() {
		return "member/login";
	}
	
	@RequestMapping(value="/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}

	/*메인 popular*/
	@RequestMapping("/mainpopular")
	public String mainPopular(Model m) {
		List<Map> list = dao.mainPopular();
		m.addAttribute("list",list);
		return "portfolio/cardPost";
	}
	
	@RequestMapping("/mainrecent")
	public String mainRecent(Model m) {
		List<Map> list = dao.mainRecent();
		m.addAttribute("list",list);
		return "portfolio/cardPost";
	}
	
	@RequestMapping("/checked")
	public @ResponseBody String notiChecked(String uname, HttpServletRequest request) {
		userdao.checked(uname);
		request.getSession().setAttribute("unread", 0);

		return "success";
		
	}
}
