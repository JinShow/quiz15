package com.example.quiz15.service.ifs;

import com.example.quiz15.vo.BasicRes;
import com.example.quiz15.vo.FeedbackRes;
import com.example.quiz15.vo.FeedbackUserRes;
import com.example.quiz15.vo.FillinReq;
import com.example.quiz15.vo.QuestionRes;
import com.example.quiz15.vo.QuizCreateReq;
import com.example.quiz15.vo.QuizUpdateReq;
import com.example.quiz15.vo.SearchReq;
import com.example.quiz15.vo.SearchRes;
import com.example.quiz15.vo.StatisticsRes;

public interface QuizService {

	//建立一個新的問卷
	public BasicRes create(QuizCreateReq req) throws Exception;
	
	//更新現有的問卷。
	public BasicRes update(QuizUpdateReq req) throws Exception;
	
	//取得所有問卷的清單
	public SearchRes getAllQuizs();
	
	//根據 quizId 取得該問卷的詳細資訊（可能包含所有題目）。
	public QuestionRes getQuizsByQuizId(int quizId);
	
	//根據搜尋條件搜尋問卷（如標題、建立日期等）。
	public SearchRes search(SearchReq req);
	
	//刪除特定問卷。
	public BasicRes delete(int quizId) throws Exception;
	
	//填寫問卷的答案（如使用者提交答題）。
	public BasicRes fillin(FillinReq req) throws Exception;
	
	//取得填過此問卷的使用者列表。
	public FeedbackUserRes feedbackUserList(int quizId);
	
	//取得問卷的統計結果回饋。
	public FeedbackRes feedback(int quizId,String email);
	
	//統計
	public StatisticsRes statistics(int quizId);
	
}
