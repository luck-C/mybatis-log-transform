package com.luck.mybstislogjoinner.controller;

import com.luck.mybstislogjoinner.dto.LogTransformDto;
import com.luck.mybstislogjoinner.service.LogService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log")
public class LogController {


    @Autowired
    private LogService logService;

    @PostMapping("/transform")
    public LogTransformDto transform(@RequestBody LogTransformDto logTransformDto){
        return logService.logParse(logTransformDto);
    }

}
