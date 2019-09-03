package com.etc.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

public class CronTaskLog {

    private Integer id;
    private Integer ctId;
    private String serverUrl;

    private String serverPath;

    private String serverMethod;

    private String serverParameter;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private Float spendTime;
    @TableField(exist = false)
    private String cateName;
    @TableField(exist = false)
    private String remark;

    private String serverStatus;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCtId() {
        return ctId;
    }

    public void setCtId(Integer ctId) {
        this.ctId = ctId;
    }


    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getServerPath() {
        return serverPath;
    }

    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }

    public String getServerMethod() {
        return serverMethod;
    }

    public void setServerMethod(String serverMethod) {
        this.serverMethod = serverMethod;
    }

    public String getServerParameter() {
        return serverParameter;
    }

    public void setServerParameter(String serverParameter) {
        this.serverParameter = serverParameter;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Float getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(Float spendTime) {
        this.spendTime = spendTime;
    }

    public String getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(String serverStatus) {
        this.serverStatus = serverStatus;
    }
}
