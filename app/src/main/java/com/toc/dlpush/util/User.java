package com.toc.dlpush.util;

import java.io.Serializable;

/**
 * Created by 袁飞 on 2015/5/21.
 * DLpush
 */
public class User implements Serializable {
    String  id;
    String  ammeterid;//户号
    String  linkman;//联系人
    String  passwd;
    String  roleid;//4 是普通用户、5区域经理
    String  companyname;//公司名称
    String  companyaddress;//公司地址
    String  phone;//手机号码
    String  isapp;//是否登录过app，是否有此人imei，0有1无
    String  line;//线路
    String  companytype;//用电类型(例如:大工业用电)
    String  companyclassify;//分类(一般是高压)
    String  managerid;//除了角色4普通用户以外其他角色的managerid是0，普通用户的managerid是所属区域经理的id



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmmeterid() {
        return ammeterid;
    }

    public void setAmmeterid(String ammeterid) {
        this.ammeterid = ammeterid;
    }

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getCompanyaddress() {
        return companyaddress;
    }

    public void setCompanyaddress(String companyaddress) {
        this.companyaddress = companyaddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIsapp() {
        return isapp;
    }

    public void setIsapp(String isapp) {
        this.isapp = isapp;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getCompanytype() {
        return companytype;
    }

    public void setCompanytype(String companytype) {
        this.companytype = companytype;
    }

    public String getCompanyclassify() {
        return companyclassify;
    }

    public void setCompanyclassify(String companyclassify) {
        this.companyclassify = companyclassify;
    }

    public String getManagerid() {
        return managerid;
    }

    public void setManagerid(String managerid) {
        this.managerid = managerid;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", ammeterid='" + ammeterid + '\'' +
                ", linkman='" + linkman + '\'' +
                ", passwd='" + passwd + '\'' +
                ", roleid='" + roleid + '\'' +
                ", companyname='" + companyname + '\'' +
                ", companyaddress='" + companyaddress + '\'' +
                ", phone='" + phone + '\'' +
                ", isapp='" + isapp + '\'' +
                ", line='" + line + '\'' +
                ", companytype='" + companytype + '\'' +
                ", companyclassify='" + companyclassify + '\'' +
                ", managerid='" + managerid + '\'' +
                '}';
    }
}
