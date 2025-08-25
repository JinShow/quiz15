package com.example.quiz15.service.ifs;

import com.example.quiz15.vo.BasicRes;
import com.example.quiz15.vo.QuestionRes;
import com.example.quiz15.vo.QuizCreateReq;
import com.example.quiz15.vo.QuizUpdateReq;
import com.example.quiz15.vo.SearchRes;

public interface QuizService {

	public BasicRes create(QuizCreateReq req) throws Exception;
	
	public BasicRes update(QuizUpdateReq req) throws Exception;
	
	public SearchRes getAllQuizs();
	
	public QuestionRes getQuizsByQuizId(int quizId);
}
