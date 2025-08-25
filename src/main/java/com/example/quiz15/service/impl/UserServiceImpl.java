package com.example.quiz15.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.quiz15.constants.ResCodeMessage;
import com.example.quiz15.dao.UserDao;
import com.example.quiz15.entity.User;
import com.example.quiz15.service.ifs.UserService;
import com.example.quiz15.vo.AddInfoReq;
import com.example.quiz15.vo.BasicRes;
import com.example.quiz15.vo.LoginReq;

@Service
public class UserServiceImpl implements UserService{
	
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); // 工具類，整個類別都用的到
	
	@Autowired
	private UserDao userDao;

	@Override
	public BasicRes addInfo(AddInfoReq req) {
		// req 的參數檢查已經在類別 User 中透過 Validation 驗證
		//1.檢查帳號是否已存在
		//透過 PK email 去取得 count 數， 只會得到 0 或 1
		int count = userDao.getCountByEmail(req.getEmail());
		if(count == 1) { //email是 PK ,若 count = 1 表示帳號已存在
			return new BasicRes(ResCodeMessage.EMAIL_EXISTS.getCode(),ResCodeMessage.EMAIL_EXISTS.getMessage());
		}
		//2.新增資訊
		try {
			userDao.addInfo(req.getName(),req.getPhone(), req.getEmail(), req.getAge(), encoder.encode(req.getPassword()));
			return new BasicRes(ResCodeMessage.SUCCESS.getCode(),ResCodeMessage.SUCCESS.getMessage());
		} catch (Exception e) {
			return new BasicRes(ResCodeMessage.ADD_INFO_ERROR.getCode(),ResCodeMessage.ADD_INFO_ERROR.getMessage());
		}
	}

	@Override
	public BasicRes login(LoginReq req) {
		//確認 email 是否存在於 DB
		User user = userDao.getByEmail(req.getEmail());
		// 透過 email 取得一筆資料，不存在的話，mail，會得到null
		if (user == null) {
			return new BasicRes(ResCodeMessage.NOT_FOUND.getCode(), ResCodeMessage.NOT_FOUND.getMessage());
		}
		//比對密碼
		//if 的條件式最前面有驚嘆號，表示整個比對結果 ==false 的意思
		if (!encoder.matches(req.getPassword(), user.getPassword())) {
			return new BasicRes(ResCodeMessage.PASSWORD_MISMATCH.getCode(),
					ResCodeMessage.PASSWORD_MISMATCH.getMessage());
		}
		return new BasicRes(ResCodeMessage.SUCCESS.getCode(), ResCodeMessage.SUCCESS.getMessage());
	}

}
