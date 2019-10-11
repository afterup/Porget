package com.porget.control;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.porget.domain.PortfolioVO;
import com.porget.domain.UserVO;
import com.porget.persistence.MyPageDAO;
import com.porget.persistence.UserDAO;

@Controller
public class UserController {
	
	@Autowired
	private MyPageDAO mypageDao;

	@Inject
	private UserDAO userDao;
	
	@RequestMapping("/showUser")
	public String showUser(Model m, String uname) { //로그인한 유저네임으로 세션값 받아 회원의 프로필사진, 회원 포트폴리오 받아오기
		m.addAttribute("uname", uname); //로그인한 세션값을 이메일 또는 유저네임으로 받아온다 세션값이라 안줘도 되긴할텐데 혹시몰라 테스트용으로 넣어줌
		String uphoto = userDao.selectUserPhoto(uname);
		m.addAttribute("uphoto", uphoto); //회원사진 받아오기
		List<Map<String, Object>> list = userDao.myPortfolio(uname);
		m.addAttribute("list", list);
		return "common/showUser";
	}

	@GetMapping("/mypage/{uname}")
	public String selectMyPage(HttpSession session, @PathVariable("uname")String uname, Model m) {
//		String uname = (String)session.getAttribute("uname");
		UserVO vo = mypageDao.selectUser(uname);
		m.addAttribute("vo",vo);
		
		List<PortfolioVO> portfolioVO = mypageDao.userPortfolio(uname);
		System.out.println(portfolioVO);
		for (int i=0;i<portfolioVO.size();i++) {
			PortfolioVO pvo =portfolioVO.get(i);
			pvo.setPfthumb(pvo.getPfthumb().split("\\|")[0]);
		}
		m.addAttribute("pvo",portfolioVO);
		
		List<PortfolioVO> portfolioVO2 = mypageDao.likePortfolio(uname);
		for(int i=0; i<portfolioVO2.size();i++) {
			PortfolioVO pvo2 = portfolioVO2.get(i);
			pvo2.setPfthumb(pvo2.getPfthumb().split("\\|")[0]);
		}
		System.out.println(portfolioVO2);
		m.addAttribute("like",portfolioVO2);
		
		return "member/myPage";
	}
	
	@RequestMapping("/mypage/changeuphoto")
	public @ResponseBody String changeUphoto(HttpServletRequest request,MultipartFile photo) {
		String removeName = (String) request.getSession().getAttribute("uphoto");
		String uname = (String) request.getSession().getAttribute("uname");
		String uploadFolder = request.getSession().getServletContext().getRealPath("/resources/files/profile/");
		File removeFile = new File(uploadFolder,removeName);
		if(removeFile.isFile()) {
			removeFile.delete();
		}
		String photoFileName = photo.getOriginalFilename();
		photoFileName = photoFileName.substring(photoFileName.lastIndexOf("\\")+1);
		UUID uuid = UUID.randomUUID();
		photoFileName = uuid.toString()+"_"+photoFileName;
		File saveFile = new File(request.getSession().getServletContext().getRealPath("/resources/files/profile/"),photoFileName);
		try {
			photo.transferTo(saveFile);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			return "fail";
		}
		HashMap<String, String> photoName = new HashMap<String, String>();
		photoName.put("uname", uname);
		photoName.put("uphoto", photoFileName);
		if(mypageDao.changeUphoto(photoName)==1) {
			request.getSession().setAttribute("uphoto", photoFileName);
			return "success";
		}
		return "fail";
	}
	
	@DeleteMapping("/mypage/{uname}")
	public ResponseEntity<String> breakMember(@PathVariable("uname")String uname) {
		
		int delCnt = mypageDao.breakMember(uname);
		return (delCnt == 1) ? new ResponseEntity<>("success",HttpStatus.OK)
				: new ResponseEntity<>("fail",HttpStatus.INTERNAL_SERVER_ERROR);
	}
	

	
	
	
	
	
	
}
