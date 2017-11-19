package com.tc.tcsocket.dao;

import java.util.List;
import java.util.Map;

import com.tc.tcsocket.model.Member;

public interface IMemberDao {

	public List<Member> queryMemberByName(String name);
	public Member queryMemberByIdAndMobile(long id,String mobile);//com.tc.tcsocket.dao.IMemberDao.queryUserByIdAndMobile
	public void add();
	public void addtest(String str);
	public int  addUser(Map map);
	public void del(int id);
	public void alter(int id);
	public long queryOtherpartyMemberId(long myMemberid);
	public Member getUserByName(Map map);
	public int getCount();
	
	 
	
	
}
