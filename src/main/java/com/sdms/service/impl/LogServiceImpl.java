package com.sdms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdms.entity.Logs;
import com.sdms.mapper.LogMapper;
import com.sdms.service.LogService;
import org.springframework.stereotype.Service;


@Service
public class LogServiceImpl extends ServiceImpl<LogMapper,Logs> implements LogService
{

}
