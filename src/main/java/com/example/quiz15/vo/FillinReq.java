package com.example.quiz15.vo;

import java.util.List;

import com.example.quiz15.constants.ConstantsMessage;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class FillinReq {

	@Min(value = 1, message = ConstantsMessage.QUIZ_ID_ERROR)
	private int quizId;
	
	@NotBlank(message = ConstantsMessage.EMAIL_ERROR)
	private String email;
	
	//不用檢查，可能問卷的問題都不是必填
	@Valid //因為 vo 裡面的 questionId 有驗證，所以要加上@Valid 才會讓該驗證生效
	private List<QuestionIdAnswerVo> QuestionAnswerVoList;

	public int getQuizId() {
		return quizId;
	}

	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<QuestionIdAnswerVo> getQuestionAnswerVoList() {
		return QuestionAnswerVoList;
	}

	public void setQuestionAnswerVoList(List<QuestionIdAnswerVo> questionAnswerVoList) {
		QuestionAnswerVoList = questionAnswerVoList;
	}
	
	
}
