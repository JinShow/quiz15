package com.example.quiz15.vo;

import java.util.List;

public class FeedbackUserRes extends BasicRes {

	private int quizId;

	// 一張問卷可能會有多個人填寫
	private List<UserVo> userVoList;

	public FeedbackUserRes() {
		super();
	}

	public FeedbackUserRes(int code, String message) {
		super(code, message);
	}

	public FeedbackUserRes(int code, String message, int quizId, List<UserVo> userVoList) {
		super(code, message);
		this.quizId = quizId;
		this.userVoList = userVoList;
	}

	public int getQuizId() {
		return quizId;
	}

	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}

	public List<UserVo> getUserVoList() {
		return userVoList;
	}

	public void setUserVoList(List<UserVo> userVoList) {
		this.userVoList = userVoList;
	}

}
