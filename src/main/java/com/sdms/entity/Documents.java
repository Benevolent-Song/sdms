package com.sdms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author LSY
 * @since 2022-04-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Documents implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文档id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * es文档id
     */
    private String pid;

    /**
     * 存放路径
     */
    private String path;

    /**
     * 类型
     */
    private String category;

    /**
     * 编号
     */
    private String number;

    /**
     * 中文标题
     */
    @TableField("titleCn")
    private String titleCn;

    /**
     * 英文标题
     */
    @TableField("titleEn")
    private String titleEn;

    /**
     * 发布方
     */
    @TableField("issuedBy")
    private String issuedBy;

    /**
     * 发布日期
     */
    @TableField("releaseDate")
    private String releaseDate;

    /**
     * 实施日期
     */
    @TableField("implementDate")
    private String implementDate;

    /**
     * 分类
     */
    private String domain;

    /**
     * 偏离页数
     */
    private Integer offset;


}
