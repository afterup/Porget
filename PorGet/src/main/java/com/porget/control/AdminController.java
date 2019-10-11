package com.porget.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.porget.domain.Criteria;
import com.porget.domain.PageDTO;
import com.porget.domain.ReportVO;
import com.porget.service.AdminService;
import com.porget.service.ReportService;

import lombok.extern.log4j.Log4j;


@Controller
@RequestMapping("/admin")
@Log4j
public class AdminController {
	
	@Autowired
	private AdminService service;
	
	@Autowired
	ReportService reportService;
	
	/*회원관리*/
	@GetMapping(value= {"/member"})
	public String selectAll(Model m,Criteria cri) {
		
		log.info("member:"+cri.getPageNum());
		log.info(cri.getAmount());
		
		m.addAttribute("list",service.selectAll(cri));
		m.addAttribute("pageMaker",new PageDTO(cri,service.getTotal(cri)));
		return "admin/adminMember";
	}
	
	@DeleteMapping("/member/{uname}")
	public ResponseEntity<String> delete(@PathVariable("uname")String uname) {
		
		log.info("delete"+uname);
		
		int delCnt = service.delete(uname);
		return (delCnt == 1) ? new ResponseEntity<>("success",HttpStatus.OK)
				: new ResponseEntity<>("fail",HttpStatus.INTERNAL_SERVER_ERROR);
	}	
	
	@GetMapping("/insert")
	public String get() {
		
		return "/reportList";
	}
	
	@PostMapping("/insert")
	public String postReport(ReportVO rvo, RedirectAttributes attrs) {
		System.out.println("ggg");
		
		System.out.println(rvo);
		
		int reportNum = reportService.insert(rvo);
		attrs.addFlashAttribute("result",reportNum);
		
		return "redirect:/portfolio";
	}
	
	
	@RequestMapping("/remove")
	public String portfolioDelete(int pfnum, RedirectAttributes attr) {// 寃뚯떆湲� �궘�젣�썑 寃뚯떆�뙋�쑝濡� �씠�룞

		if(reportService.removePortpolio(pfnum)) {
			attr.addFlashAttribute("result","success");
		}
		return "redirect:/portfolio";
	}
	@RequestMapping(value="reportList")
	public String reportList(Model model,Criteria cri) {
		model.addAttribute("list", reportService.selectReportPage(cri));
		model.addAttribute("pageMaker",new PageDTO(cri, reportService.reportTotal(cri)));
		
		return "admin/reportList";
	}
	
	@RequestMapping("/selectReport")
	public @ResponseBody ReportVO selectReport(int reportNum) {
		System.out.println(reportNum);
		ReportVO vo = reportService.selectReport(reportNum);
		System.out.println(vo.getReportContext());
		System.out.println(vo);
		return vo;
	}
	
	@RequestMapping("/removeUser")
	public @ResponseBody String removeUser(String uname) {
		if(reportService.removeUser(uname)) return uname+"�떂�씠 �궘�젣�릺�뿀�뒿�땲�떎.";
		else return "�궘�젣�뿉 �떎�뙣�뻽�뒿�땲�떎.";
	}
	
	@RequestMapping("/updateResult")
	public @ResponseBody boolean updateResult(int reportNum) {
		return reportService.updateReport(reportNum);
	}
	
}
