package com.luck.mybstislogjoinner.service.impl;

import com.luck.mybstislogjoinner.dto.LogTransformDto;
import com.luck.mybstislogjoinner.logline.LogLineParser;
import com.luck.mybstislogjoinner.service.LogService;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl implements LogService {

    @Override
    public LogTransformDto logParse(LogTransformDto logTransformDto) {
        final String parse = LogLineParser.parse(logTransformDto);
        logTransformDto.setResultLog(parse);
        return logTransformDto;
    }
}
