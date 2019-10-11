package com.porget.service;

import java.util.List;

import com.porget.domain.Criteria;
import com.porget.domain.ReportVO;

public interface ReportService {
	
	public int insert(ReportVO rvo);
	public ReportVO get(int reportNum);

	public List<ReportVO> selectReportPage(Criteria cri);//�떊怨좉� 議고쉶
	public ReportVO selectReport(int reportNum);//�떊怨좊궡�슜 �솗�씤
	
	public int reportTotal(Criteria cri);
	
	public boolean removePortpolio(int pfnum);
	
	public boolean removeUser(String uname);
	
	public boolean updateReport(int reportNum);
}
