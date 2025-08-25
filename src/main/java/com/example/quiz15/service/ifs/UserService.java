package com.example.quiz15.service.ifs;

import com.example.quiz15.vo.AddInfoReq;
import com.example.quiz15.vo.BasicRes;
import com.example.quiz15.vo.LoginReq;

public interface UserService {

	public BasicRes addInfo(AddInfoReq req);
	
	public BasicRes login(LoginReq req);
}
