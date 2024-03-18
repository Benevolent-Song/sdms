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

    private String titleCn;//中文标题

    private String number;//文档标准编号

    private String peole;//操作人员

    private String dotime;//操作日期

    private String releaseDate;//操作的内容
}
