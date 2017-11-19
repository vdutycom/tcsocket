package com.tc.tcsocket.model;

import com.tc.tcsocket.interfaces.IMember;

public class Member {
	IMember imember;
	private long id;
	public long getMemberId() {
		return id;
	}
	public void setMemberId(long memberId) {
		this.id = memberId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	private String  mobile;
	
	/**
	 * MyBatis查询时需要
	 */
	public Member()
	{}
	
	public Member(long id,String mobile,IMember imember)
	{
		this.imember =imember;
		this.id = id;
				this.mobile = mobile;
		
	}
	
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	private int  sex;
	private String photo;
	
	
	
	/**
	 * 用户登录 
	 */
	public void login()
	{
		//test
				long memberid_epl = 68;
				String mobile_epl="13859989563";
				if (this.id ==memberid_epl && this.mobile == mobile_epl)
				{
					imember.loginDoWidth(String.valueOf(this.id),this.mobile);
				}
		
	}
	private long request_memberid;
	public long getRequest_memberid() {
		return request_memberid;
	}
	public void setRequest_memberid(long request_member) {
		this.request_memberid = request_member;
	}
	public int getApply_status() {
		return apply_status;
	}
	public void setApply_status(int apply_status) {
		this.apply_status = apply_status;
	}
	private int apply_status;
	
	
	
	
	
	

}
