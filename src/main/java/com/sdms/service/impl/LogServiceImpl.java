package com.sdms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdms.entity.Documents;
import com.sdms.entity.Logs;
import com.sdms.mapper.DocumentsMapper;
import com.sdms.mapper.LogMapper;
import com.sdms.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;


@Service
public class LogServiceImpl extends ServiceImpl<LogMapper,Logs> implements LogService
{

}
