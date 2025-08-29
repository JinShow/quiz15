package com.example.quiz15.vo;

import java.util.ArrayList;
import java.util.List;

import com.example.quiz15.constants.ConstantsMessage;

import jakarta.validation.constraints.Min;

// 此 class 將問題編號和作答綁在一起
public class QuestionIdAnswerVo {

	@Min(value = 1, message = ConstantsMessage.QUIZ_ID_ERROR)
	private int questionId;

	//給定預設值:answerList 的預設值會從 null 變成空的 List
	private List<String> answerList = new ArrayList<>();

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public List<String> getAnswerList() {
		return answerList;
	}

	public void setAnswerList(List<String> answerList) {
		this.answerList = answerList;
	}
	
	
}
