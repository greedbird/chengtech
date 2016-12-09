package com.chengtech.chengtechmt.entity;

import java.io.Serializable;

public class User implements Serializable {

	private static final String NO = "0";
	private static final String YES = "1";
	public String id;
	public String name;				   //用户名
	public String userAccount;			//帐号
	public String deptId;				//部门id
	public String deptIds;				//部门ids
	public String deptNames;			//部门名称s 
	public String userNo;				//员工编号
	public String question;			//取回密码问题
	public String answer;				//取回密码答案
	public String sex;					//性别
	public String jobPosition;			//职位
	public String birthday;				//出生日期
	public String userState;			//员工状态
	public String unUsed = NO;			//是否禁用
	
	//联系方式
	public String officeTel;			//工作电话
	public String officeFax;			//工作传真
	public String officeAddress;		//工作地址
	public String mobile;				//移动电话
	public String homeTel;				//住宅电话
	public String homeAddress;			//住宅地址
	public String postalCode;			//邮编
	public String blog;				//个人博客
	public String email;				//Email
	
	//其他
	public String identityName;		//证件名称
	public String identityCode;		//证件号码
	public String education;			//学历
	public String degree;				//学位
	public String university;			//毕业院校
	public String certificate;			//获得证书
	
	public String lastLoginTime;			//最后登录时间
	public Long loginCount;			//登录次数
	
	public String onlineState;			//在线状态
	public String systemDeptId;		   //部门ID
	public String systemDeptName;		//部门名称
	public Boolean isSuperUser;		   //是不是超级用户
	
	public String linkman;						//联系人姓名
	public String userType ;					//用户类型
	public String isApproved ;					//是否通过审批
	public String approvedDate;					//审批日期
	public String isCompanyManager ;			//是否为单位管理员
	public String registerDate;					//注册时间
	public String deptName;						//单位名称
}
