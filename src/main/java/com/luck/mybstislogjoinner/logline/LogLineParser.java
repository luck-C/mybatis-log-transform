package com.luck.mybstislogjoinner.logline;

import com.google.common.primitives.Primitives;
import com.luck.mybstislogjoinner.dto.LogTransformDto;
import com.luck.mybstislogjoinner.exception.CommonException;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class LogLineParser {

    static final String STATEMENT_PREFIX = "Preparing:";
    static final String PARAMETERS_PREFIX = "Parameters:";
    static final String PARAMETER_SEPARATOR = ",";
    static final String PARAMETER_PLACEHOLDER = "\\?";
    static final Pattern PARAMETER_PATTERN = Pattern.compile("(.*)\\((.+)\\)");
    static final Set<String> RAW_PARAMETER_TYPES = Primitives.allWrapperTypes().stream().map(Class::getSimpleName).collect(Collectors.toSet());

    public static String parse(LogTransformDto logTransformDto){
        String sqlLog = logTransformDto.getRowLog();
        if(!org.springframework.util.StringUtils.hasText(sqlLog)){
            throw new CommonException("sql日志为空");
        }

        try (ByteArrayInputStream sqlIn = new ByteArrayInputStream(org.apache.tomcat.util.codec.binary.StringUtils.getBytesUtf8(sqlLog));
             InputStreamReader transform = new InputStreamReader(sqlIn);
             BufferedReader sqlReader = new BufferedReader(transform)){
             LogLineCombiner combiner = new LogLineCombiner();
            final String collectSql = sqlReader.lines()
                    .filter(line -> Objects.nonNull(line))
                    .map(combiner)
                    .filter(Objects::nonNull)
                    .map(LogLineHolder::formattedSqlStatement)
                    .collect(Collectors.joining());

            return collectSql;
        } catch (Exception e) {
            throw new CommonException("解析sql日志异常");
        }
    }


    /**
     * 获取sql语句
     * @return
     */
    public static Optional<String>  extractLineStatement(String data){
        return extractPayLoad(data,STATEMENT_PREFIX);
    }


    public static Optional<String> extractLineParameter(String data) {

        return extractPayLoad(data,PARAMETERS_PREFIX);
    }

    private static Optional<String> extractPayLoad(String logLine,String identifyPrefix){
        int prefixIdx = logLine.indexOf(identifyPrefix);
        if(prefixIdx < 0){
            return Optional.empty();
        }
        String payLoad = logLine.substring(prefixIdx + identifyPrefix.length());
        return Optional.of(payLoad);

    }

    public static List<String> parseParameter(String parameterLine) {
        if(StringUtils.isBlank(parameterLine)){
            return Collections.emptyList();
        }

        parameterLine = parameterLine.trim();

        return (List<String>) Arrays.stream(StringUtils.split(parameterLine, PARAMETER_SEPARATOR))
                .map(String::trim)
                .map(t->{
                    if(Objects.equals("null",t)){
                        return new Pair<>("null",null);
                    }
                    Matcher matcher = PARAMETER_PATTERN.matcher(t);
                    if(!matcher.matches()){
                        return new Pair<>("","");
                    }
                    return new Pair<>(matcher.group(1),matcher.group(2));
                }).map(x->formatParameter((Pair<String, String>) x))
                .collect(Collectors.toList());
    }


    private static String formatParameter(Pair<String,String > paramInfo){
        String val = paramInfo.getKey();
        String type = paramInfo.getValue();
        /*if(Objects.isNull(type) || RAW_PARAMETER_TYPES.contains(type)){
            return val;
        }*/
        return val;
    }


    static String formatStatement(String statement, List<String> params) {
        for (String param : params) {
            statement = statement.replaceFirst(PARAMETER_PLACEHOLDER, param);
        }
        return statement;
    }
}
