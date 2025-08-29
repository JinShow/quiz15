package com.example.quiz15.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.quiz15.entity.Quiz;

import jakarta.transaction.Transactional;

@Repository
public interface QuizDao extends JpaRepository<Quiz, Integer>{

	@Modifying
	@Transactional
	@Query(value = "insert into quiz(name, description, start_Date, end_Date, is_published) " //
			+ " values(?1, ?2, ?3, ?4, ?5)" , nativeQuery = true)
	public void insert (String name, String description, LocalDate startDate, LocalDate endDate, boolean published);
	
	@Query(value = "select max(id) from quiz" , nativeQuery = true)
	public int getMaxQuizId();
	
	@Query(value = "select count(id) from quiz where id = ?1" , nativeQuery = true)
	public int getCountByQuizId(int quizId);
	
	/**
	 * 回傳值的資料型態設定成 int 主要是用來判斷資料是否有正確更新成功，
	 * int = 1 表示有資料被更新 ; 0 則表示無資料被更新
	 */
	@Modifying
	@Transactional
	@Query(value = "update quiz set name=?2, description=?3, start_Date=?4, end_Date=?5, is_published=?6 where id = ?1" , nativeQuery = true)
	public int update(int id, String name, String description, LocalDate startDate, LocalDate endDate, boolean published);
	
	@Query(value = "select * from quiz" , nativeQuery = true)
	public List<Quiz> getAll();
	
	@Query(value = "select * from quiz where name like %?1% and start_Date >= ?2" //
			+ " and end_date <= ?3", nativeQuery = true)
	public List<Quiz> getAll(String name,LocalDate startDate, LocalDate endDate);
	
	//is_published = true : is 只能用在boolean 值的欄位， is 也可以替換成等於(=)
	@Query(value = "select * from quiz where name like %?1% and start_Date >= ?2" //
			+ " and end_date <= ?3 and is_published is true", nativeQuery = true)
	public List<Quiz> getAllPublished(String name,LocalDate startDate, LocalDate endDate);
	
	@Modifying
	@Transactional
	@Query(value = "delete from quiz where id = ?1" , nativeQuery = true)
	public void deleteById(int id);
	
	@Query(value = "select * from quiz where id = ?1" , nativeQuery = true)
	public Quiz getById (int id);
	
	// 判斷該問卷是否已發佈及當下日期是否介於開始日期和結束日期之間 //count是資料筆數 跟QUIZ表撈資料比對正確會=1  0=沒資料
	@Query(value = "select count(id) from quiz where id = ?1 and ?2 >= start_date and ?2<= end_date and is_published is true" , nativeQuery = true)
	public int selectCountById(int id, LocalDate now);
}
