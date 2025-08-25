package com.example.quiz15.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.quiz15.entity.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserDao extends JpaRepository<User, String> {

	//這個只能比對筆數
	@Query(value = "select count(email) from user where email = ?1", nativeQuery = true)
	public int getCountByEmail(String email);

	// 登入 這撈*可以比對帳號、密碼
	@Query(value = "select * from user where email = ?1", nativeQuery = true)
	public User getByEmail(String email);

	// 有異動到資料庫的值就一定要加@Modifying、@Transactional
	@Modifying
	@Transactional
	@Query(value = "insert into user(name, phone, email, age, password) values" //
			+ "(?1, ?2, ?3, ?4, ?5)", nativeQuery = true)
	public void addInfo(String name, String phone, String email, int age, String password);

}
