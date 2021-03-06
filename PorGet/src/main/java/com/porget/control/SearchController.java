package com.porget.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.porget.persistence.PortfolioDAO;

@Controller
public class SearchController {

	@Autowired
	private PortfolioDAO dao;

	@RequestMapping("/searchKey")
	public String searchKeyword(Model m, String keyword) { //검색창에서 연관검색어 가져오기
		List<Map<String, Object>> list = dao.searchKeyword(keyword);
		m.addAttribute("list", list);
		return "search/searchInputPartTest";
	}
	
	@RequestMapping("/searchButton")
	public String searchButton(String keyword, Model m) {//검색버튼 눌렀을시 검색결과 가져오기
		System.out.println("키워드얌 검색창-->"+keyword);
		if(keyword.contains("#")) {
			m.addAttribute("keyword", keyword);
			return "search/searchResult";
		}else {
			
			if(!dao.searchName(keyword).isEmpty()&&!dao.searchTag(keyword).isEmpty()) {
			m.addAttribute("tagOrName", "both");
			m.addAttribute("input", keyword);
			return "search/searchResultAll";
			
			}else if(!dao.searchTag(keyword).isEmpty()&&dao.searchName(keyword).isEmpty()) {
			m.addAttribute("tagOrName", "tag");
			m.addAttribute("input", keyword);
			return "search/searchResultAll";
			
			}else if(!dao.searchName(keyword).isEmpty()&&dao.searchTag(keyword).isEmpty()) {
			System.out.println("dao.searchName(keyword)"+dao.searchName(keyword));
			m.addAttribute("tagOrName", "name");
			m.addAttribute("input", keyword);
			return "search/searchResultAll";	
			}
			return "search/searchResultAll";
		}
		
	}
	
	@RequestMapping("/searchTagBox")
	public String searchTagBox(Model m, String keyword) { //검색창에서 해시태그 연관검색어 가져오기
		List<String> list = dao.searchTag(keyword);
		m.addAttribute("list", list);
		m.addAttribute("keyword",keyword);
		return "search/searchTagBox";
	}
	
	@RequestMapping("/searchNameList")
	public String searchNameList(Model m, String input) { //검색시 제목에 검색어가 포함되어있을때
		List<List<Map<String, Object>>> list4 = new ArrayList<List<Map<String,Object>>>();
		List<Map<String, Object>> list=null;
			list4.add(dao.searchNameList(input));
			for (int i = 0; i < list4.size(); i++) {
				for (int j = 0; j < list4.get(i).size(); j++) {
					Map<String,Object> map = list4.get(i).get(j);
					map.put("PFTHUMB",((String)map.get("PFTHUMB")).split("\\|")[0]);				
				}
			}
			m.addAttribute("list4", list4);
		return "search/searchNameResult";
	}

	@RequestMapping("/searchTagList")
	public String searchTagList(Model m, String input) {//검색시 태그에 검색어가 포함되어있을때
		List<List<Map<String, Object>>> list4 = new ArrayList<List<Map<String,Object>>>();
		List<Map<String, Object>> list=null;
		list4.add(dao.searchTagList(input));
		for (int i = 0; i < list4.size(); i++) {
			for (int j = 0; j < list4.get(i).size(); j++) {
				Map<String,Object> map = list4.get(i).get(j);
				map.put("PFTHUMB",((String)map.get("PFTHUMB")).split("\\|")[0]);				
			}
		}
		m.addAttribute("list4", list4);
		return "search/searchTagResult";
	}
	
	@RequestMapping("/searchHashTagList")
	public String searchHashTagList(Model m, String keyword, int base) {//검색시 태그에 검색어가 포함되어있을때
			String[] keyword2 = keyword.split("#"); 
			List<Map<String, Object>> list=null;
			if(keyword2.length==2) {
				list = dao.searchHashResult(keyword2[1], base);
			}else {
				String keyword3 = keyword2[1];
				for (int i = 2; i < keyword2.length; i++) {
						keyword3 = keyword3 +"|"+ keyword2[i];
				}
				System.out.println("keyword33>>>"+keyword3);
				list = dao.searchHashResult(keyword3, base);
			}
			System.out.println("list 0903>>>"+ list);
			for (int i = 0; i < list.size(); i++) {
				Map<String,Object> map = list.get(i);
				map.put("PFTHUMB",((String)map.get("PFTHUMB")).split("\\|")[0]);
			}
			m.addAttribute("list", list);
		return "search/searchHashResult";
	}
	
	@RequestMapping("/searchHashClick")
	public String searchHashClick(Model m, String keyword) {//게시물에서 해시태그 클릭시
		m.addAttribute("HashTag", keyword);
		return "search/searchHashClick";
	}
	
	@RequestMapping("/searchHashClickContent")
	public String searchHashClickContent(Model m, String keyword, int base) {//게시물에서 해시태그 클릭시
		List<Map<String, Object>> list = dao.searchHashResult(keyword, base);
		for (int i = 0; i < list.size(); i++) {
			Map<String,Object> map = list.get(i);
			map.put("PFTHUMB",((String)map.get("PFTHUMB")).split("\\|")[0]);
		}
		m.addAttribute("list", list);
		m.addAttribute("HashTag", keyword);
		return "search/searchHashClickContent";
	}
	
}
