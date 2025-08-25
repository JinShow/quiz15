package com.example.quiz15.vo;

import java.time.LocalDate;

public class SearchReq {

	private String quizName;
	
	private LocalDate startDate;
	
	private LocalDate endDate;

	public SearchReq() {
		super();
	}

	public SearchReq(String quizName, LocalDate startDate, LocalDate endDate) {
		super();
		this.quizName = quizName;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public String getQuizName() {
		return quizName;
	}

	public void setQuizName(String quizName) {
		this.quizName = quizName;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	
	
}
