package com.example.quiz15.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.quiz15.constants.QuestionType;
import com.example.quiz15.constants.ResCodeMessage;
import com.example.quiz15.dao.QuestionDao;
import com.example.quiz15.dao.QuizDao;
import com.example.quiz15.entity.Question;
import com.example.quiz15.entity.Quiz;
import com.example.quiz15.service.ifs.QuizService;
import com.example.quiz15.vo.BasicRes;
import com.example.quiz15.vo.QuestionRes;
import com.example.quiz15.vo.QuestionVo;
import com.example.quiz15.vo.QuizCreateReq;
import com.example.quiz15.vo.QuizUpdateReq;
import com.example.quiz15.vo.SearchReq;
import com.example.quiz15.vo.SearchRes;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Service
public class QuizServiceImpl implements QuizService {

	// 提供 類別(或 Json 格式)與物件之間的轉換
	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private QuizDao quizDao;

	@Autowired
	private QuestionDao questionDao;

	/**
	 * @Transactional:事務</br>
	 *                        1.當一個方法中執行多個 Dao 時(跨表或是同一張表寫多筆資料)，這些所有的資料應該都要算同一次行為，
	 *                        所以這些資料要嘛全部寫入成功，不然就全部寫入失敗 2.@Transactional 有效回溯的異常預設是
	 *                        RunTimeException,若發生的異常不是RunTimeException
	 *                        或其子類別的異常類型,資料皆不會回溯,因此想要讓只要發生任何一種異常時資料都要可以回溯，
	 *                        可以將@Transactional的有效範圍從RunTimeException提高至Exception
	 */
	@Transactional(rollbackOn = Exception.class)
	@Override
	public BasicRes create(QuizCreateReq req) throws Exception {
		// 參數檢查已透過 @Valid 驗證了
		try {
			// 檢查日期:使用排除法 開始在結束後req.getStartDate().isAfter(req.getEndDate())
			BasicRes checkRes = checkDate(req.getStartDate(), req.getEndDate());
			if (checkRes.getCode() != 200) { // 不等於 200 表示檢查出有錯誤
				return checkRes;
			}
			// =======================================================這段隱藏到checkDate了
//			//1.開始日期不能比結束日期晚  2.開始日期不能比當前創建的日期早
//			//判斷式:假設 開始日期比結束日期晚 或 開始日期比當前日期早 -->回錯誤訊息
//			//LocalDate.now() -->取得當前的日期
//			if(req.getStartDate().isAfter(req.getEndDate()) || req.getStartDate().isBefore(LocalDate.now())){
//				return new BasicRes(ResCodeMessage.DATE_FORMAT_ERROR.getCode(),ResCodeMessage.DATE_FORMAT_ERROR.getMessage());
//			}
			// ========================================================
			// 新增問卷
			quizDao.insert(req.getName(), req.getDescription(), req.getStartDate(), req.getEndDate(),
					req.isPublished());
			// 新增完問卷後，取得問卷流水號
			// 雖然因為 @Transactional 尚未將資料提交(commit)進資料庫，但實際上SQL語法已經執行完畢，
			// 依然可以取得對應的值
			int quizId = quizDao.getMaxQuizId();
			// 新增問題
			// 取出問卷中的所有問題
			List<QuestionVo> questionVoList = req.getQuestionList();
			// 處理每一題問題
			for (QuestionVo vo : questionVoList) {
				// 檢查題目類型與選項
				checkRes = checkQuestionType(vo);
				// 呼叫方法checkQuestionType得到的 res 若是 null ，表示檢查都沒有問題
				// 因為方法中檢查到最後都沒有問題時是回傳成功
				if (checkRes.getCode() != 200) {
//					return checkRes;
					// 因為前面已經執行了 quizDao.insert 了，所以這邊要拋出 Exception
					// 才會讓 @Transactional 生效
					throw new Exception(checkRes.getMessage());
				}
				// 因為 MySQL 沒有 List的資料格式 ，所以先把options 資料格式從 List<String> 轉成 String
				List<String> optionsList = vo.getOptions();
				String str = mapper.writeValueAsString(optionsList);
				// 要記得設定quizId
				questionDao.insert(quizId, vo.getQuestionId(), vo.getQuestion(), vo.getType(), vo.isRequired(), str);

			}
			return new BasicRes(ResCodeMessage.SUCCESS.getCode(), ResCodeMessage.SUCCESS.getMessage());
		} catch (Exception e) {
			// 不能 return BasicRes 而是要將發生的異常拋出去，這樣 @Transaction 才會生效
			throw e;
		}
	}

	private BasicRes checkQuestionType(QuestionVo vo) {
		// 1.檢查type是否是規定的類型
		String type = vo.getType();
		System.out.println(!type.equalsIgnoreCase(QuestionType.SINGLE.getType()));
		System.out.println(!type.equalsIgnoreCase(QuestionType.MULTI.getType()));
		System.out.println(!type.equalsIgnoreCase(QuestionType.TEXT.getType()));
		// 假設 從 vo 取出的 type 不符合定義的3種類型的其中一種，就返回錯誤訊息
		if (!(type.equalsIgnoreCase(QuestionType.SINGLE.getType()) //
				|| type.equalsIgnoreCase(QuestionType.MULTI.getType()) //
				|| type.equalsIgnoreCase(QuestionType.TEXT.getType()))) {
			return new BasicRes(ResCodeMessage.QUESTION_TYPE_ERROR.getCode(),
					ResCodeMessage.QUESTION_TYPE_ERROR.getMessage());
		}
		// 2.type 是單選或多選的時候，選項(options)至少要有兩個
		// 假設 type 不等於 TEXT --> 就表示type是單選或多選
		if (!type.equalsIgnoreCase(QuestionType.TEXT.getType())) {
			// 單選或多選時，選項至少要有兩個
			if (vo.getOptions().size() < 2) {
				return new BasicRes(ResCodeMessage.OPTIONS_INSUFFICIENT.getCode(),
						ResCodeMessage.OPTIONS_INSUFFICIENT.getMessage());
			}
		} else {// else --> type 是 text --> 選項應該是 null 或是 size = 0
				// 假設 選項不是 null 或 選項的 List 中有值
			if (vo.getOptions() != null && vo.getOptions().size() > 0) {
				return new BasicRes(ResCodeMessage.TEXT_HAS_OPTIONS_ERROR.getCode(),
						ResCodeMessage.TEXT_HAS_OPTIONS_ERROR.getMessage());
			}
		}
		return new BasicRes(ResCodeMessage.SUCCESS.getCode(), ResCodeMessage.SUCCESS.getMessage());
	}

	private BasicRes checkDate(LocalDate startDate, LocalDate endDate) {
		// 1.開始日期不能比結束日期晚 2.開始日期不能比當前創建的日期早
		// 判斷式:假設 開始日期比結束日期晚 或 開始日期比當前日期早 -->回錯誤訊息
		// LocalDate.now() -->取得當前的日期
		if (startDate.isAfter(endDate) || startDate.isBefore(LocalDate.now())) {
			return new BasicRes(ResCodeMessage.DATE_FORMAT_ERROR.getCode(),
					ResCodeMessage.DATE_FORMAT_ERROR.getMessage());
		}
		return new BasicRes(ResCodeMessage.SUCCESS.getCode(), ResCodeMessage.SUCCESS.getMessage());
	}

	@Transactional(rollbackOn = Exception.class)
	@Override
	public BasicRes update(QuizUpdateReq req) throws Exception {
		// 參數檢查已透過 @Valid 驗證了

		// 更新是對已存在的問卷進行修改
		try {
			// 1.檢查 quizId 是否存在
			int quizId = req.getQuizId(); // 因為後面會一直用到所以先取出
			int count = quizDao.getCountByQuizId(quizId);
			if (count != 1) {
				return new BasicRes(ResCodeMessage.NOT_FOUND.getCode(), ResCodeMessage.NOT_FOUND.getMessage());
			}
			// 2.檢查日期
			BasicRes checkRes = checkDate(req.getStartDate(), req.getEndDate());
			if (checkRes.getCode() != 200) { // 不等於 200 表示檢查出有錯誤
				return checkRes;
			}
			// 3.更新問卷
			int updateRes = quizDao.update(quizId, req.getName(), req.getDescription(), //
					req.getStartDate(), req.getEndDate(), req.isPublished());
			if (updateRes != 1) { // 表示資料沒被更新成功
				return new BasicRes(ResCodeMessage.QUIZ_UPDATE_FAILED.getCode(),
						ResCodeMessage.QUIZ_UPDATE_FAILED.getMessage());
			}
			// 4.刪除同一張問卷的所有問題
			questionDao.deleteByQuizId(quizId);

			// 5.檢查問題
			List<QuestionVo> questionVoList = req.getQuestionList();
			for (QuestionVo vo : questionVoList) {
				// 檢查題目類型與選項
				checkRes = checkQuestionType(vo);
				// 方法中檢查到最後都沒有問題時是回傳成功
				if (checkRes.getCode() != 200) {
					// 因為前面已經執行了 quizDao.insert 了，所以這邊要拋出 Exception
					// 才會讓 @Transactional 生效
					throw new Exception(checkRes.getMessage());
				}
				// 因為 MySQL 沒有 List的資料格式 ，所以先把options 資料格式從 List<String> 轉成 String
				List<String> optionsList = vo.getOptions();
				String str = mapper.writeValueAsString(optionsList);
				// 要記得設定quizId
				questionDao.insert(quizId, vo.getQuestionId(), vo.getQuestion(), vo.getType(), vo.isRequired(), str);
			}
		} catch (Exception e) {
			// 不能 return BasicRes 而是要將發生的異常拋出去，這樣 @Transaction 才會生效
			throw e;
		}
		return new BasicRes(ResCodeMessage.SUCCESS.getCode(), ResCodeMessage.SUCCESS.getMessage());
	}

	@Override
	public SearchRes getAllQuizs() {
		List<Quiz> list = quizDao.getAll();
		return new SearchRes(ResCodeMessage.SUCCESS.getCode(), ResCodeMessage.SUCCESS.getMessage(), list);
	}

	@Override
	public QuestionRes getQuizsByQuizId(int quizId) {
		if (quizId <= 0) {
			return new QuestionRes(ResCodeMessage.QUIZ_ID_ERROR.getCode(), ResCodeMessage.QUIZ_ID_ERROR.getMessage());
		}
		List<QuestionVo> questionVoList = new ArrayList<>();
		List<Question> list = questionDao.getQuestionsByQuizId(quizId);
		// 把每題選項的資料型態從String 轉換成List<String>
		for (Question item : list) {
			String str = item.getOptions();
			try {
				List<String> optionList = mapper.readValue(str, new TypeReference<>() {
				});
				// 將從DB取得的每一筆資料(Question item) 的每個欄位值放到 QuestionVo 中，以便返回給每個使用者
				// Question 和 QuestionVo的差別在於選項的資料型態 (option 和 optionList)
				QuestionVo vo = new QuestionVo(item.getQuizId(), item.getQuestionId(), item.getQuestion(), //
						item.getType(), item.isRequired(), optionList);
				// 把每個 vo 放到questionVoList 中
				questionVoList.add(vo);
			} catch (Exception e) {
				// 這邊不寫throw e 是因為此方法中沒有使用 @Transactional，不影響返回結果
				return new QuestionRes(ResCodeMessage.OPTIONS_TRANSFER_ERROR.getCode(),
						ResCodeMessage.OPTIONS_TRANSFER_ERROR.getMessage());
			}
		}
		return new QuestionRes(ResCodeMessage.SUCCESS.getCode(), ResCodeMessage.SUCCESS.getMessage(), questionVoList);
	}

	@Override
	public SearchRes search(SearchReq req) {
		// 轉換 req 的值
		// 若 quizName 是 null，轉成空字串
		String quizName = req.getQuizName();
		if (quizName == null) {
			quizName = "";
		} else { // 多餘的不需要寫，但為了理解下面的3元運算子而寫
			quizName = quizName;
		}
		// 3元運算子:
		// 格式: 變數名稱 = 條件判斷式 ? 判斷式結果為 true 時要賦值: 判斷式結果為 false 時要賦予的值
		quizName = quizName == null ? "" : quizName;
		// 上面的程式碼可以只用下面一行來取得值
		String quizName1 = req.getQuizName() == null ? "" : req.getQuizName();
		// ==================================================================================
		// 轉換 開始日期 --> 若沒有給開始日期 --> 給定一個很早的時間
		LocalDate startDate = req.getStartDate() == null ? LocalDate.of(1970, 1, 1) //
				: req.getStartDate();

		LocalDate endDate = req.getEndDate() == null ? LocalDate.of(2999, 12, 31) //
				: req.getEndDate();
		List<Quiz> list = new ArrayList<>();
		if(req.isPublished()) {
			list = quizDao.getAllPublished(quizName, startDate, endDate);
		}else {
			list = quizDao.getAll(quizName, startDate, endDate);
		}
		return new SearchRes(ResCodeMessage.SUCCESS.getCode(), ResCodeMessage.SUCCESS.getMessage(), list);
	}

	//刪除問卷、問題
	@Transactional(rollbackOn = Exception.class)
	@Override
	public BasicRes delete(int quizId) throws Exception{
		if (quizId <= 0) {
			return new BasicRes(ResCodeMessage.QUIZ_ID_ERROR.getCode(), ResCodeMessage.QUIZ_ID_ERROR.getMessage());
		}
		try {
			quizDao.deleteById(quizId);
			questionDao.deleteByQuizId(quizId);
		} catch (Exception e) {
			// 不能 return BasicRes 而是要將發生的異常拋出去，這樣 @Transaction 才會生效
			throw e;
		}
		return null;
	}

}
