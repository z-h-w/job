package com.etc.entity;

import com.baomidou.mybatisplus.annotations.TableField;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.Date;


public class CronTask {

    private Integer id;
    @Min(value = 1, message = "请选择分类")
    private Integer cateId;
    @NotEmpty(message = "请输入cron表达式")
    private String cronExpression;
    @NotEmpty(message = "请输入服务器地址")
    private String serverUrl;

    @NotEmpty(message = "请输入请求路径")
    private String serverPath;

    @NotEmpty(message = "请输入请求方式")
    private String serverMethod;

    private String parameterType;

    private String serverParameter;


    private Integer status;
    private String remark;
    private Date createTime;
    private Date updateTime;
    @TableField(exist = false)
    private String cateName;

    public CronTask(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCateId() {
        return cateId;
    }

    public void setCateId(Integer cateId) {
        this.cateId = cateId;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }


    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }
}
