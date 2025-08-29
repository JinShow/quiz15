package com.example.quiz15.vo;

import java.util.List;

public class FeedbackRes extends BasicRes{

	//這些在User Vo已提供
//	private String quizname;
//	private String description;
//	private String userName;
//	private String phone;
//	private String email;
//	private int age;
	
	private List<QuestionAnswerVo> questionAnswerVoList;
	
	

	public FeedbackRes() {
		super();
	}

	public FeedbackRes(int code, String message) {
		super(code, message);
	}

	public FeedbackRes(int code, String message, List<QuestionAnswerVo> questionAnswerVoList) {
		super(code, message);
		this.questionAnswerVoList = questionAnswerVoList;
	}

	public List<QuestionAnswerVo> getQuestionAnswerVoList() {
		return questionAnswerVoList;
	}

	public void setQuestionAnswerVoList(List<QuestionAnswerVo> questionAnswerVoList) {
		this.questionAnswerVoList = questionAnswerVoList;
	}
	
	
}
