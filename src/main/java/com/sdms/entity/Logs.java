package com.sdms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("logs")
public class Logs {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;//记录的序号,设置为主键,递增
    @TableField("titleCn")
    private String titleCn;//中文标题
    @TableField("number")
    private String number;//文档标准编号
    @TableField("people")
    private String people;//操作人员
    @TableField("dotime")
    private String dotime;//操作日期
    @TableField("whatdo")
    private String whatdo;//操作的内容
    @TableField("doclass")
    private String doclass;//进行了什么操作
}
