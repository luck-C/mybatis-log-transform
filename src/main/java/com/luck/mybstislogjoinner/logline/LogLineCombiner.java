package com.luck.mybstislogjoinner.logline;

import com.luck.mybstislogjoinner.exception.CommonException;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * 中间类
 */
public class LogLineCombiner implements Function<String,LogLineHolder> {

    private LogLineHolder logHolderCombiner = null;

    @Override
    public LogLineHolder apply(String data) {

        Optional<String> statementLineOpt = LogLineParser.extractLineStatement(data);
        Optional<String> parameterLineOpt = LogLineParser.extractLineParameter(data);
        boolean statementLineOptPresentFlag = statementLineOpt.isPresent();
        boolean parameterLineOptPresentFlag = parameterLineOpt.isPresent();
        if((statementLineOptPresentFlag && parameterLineOptPresentFlag) || (!statementLineOptPresentFlag && !parameterLineOptPresentFlag)){
            throw new CommonException("格式错误，请检查格式后重试！");
        }
        if(statementLineOptPresentFlag){
          return  swapWithStatementLine(LogLineParser.extractLineStatement(data).get());
        }
        return swapWithParameterLine(LogLineParser.extractLineParameter(data).get());
    }

    //包装后返回
    private LogLineHolder swapWithStatementLine(String statementLine) {
        LogLineHolder logLineHolderTemp = this.logHolderCombiner;
        if (logLineHolderTemp == null) {
            logHolderCombiner = logHolderCombiner.valueOf(statementLine);
        }
        return logLineHolderTemp;
    }

    private LogLineHolder swapWithParameterLine(String parameterLine){
        LogLineHolder logHolderCombiner = this.logHolderCombiner;
        if(Objects.nonNull(logHolderCombiner)){
            logHolderCombiner.setParameterLine(parameterLine);
            //这里语句和参数要一一对应，因此参数处理后要把对象设置为null
            this.logHolderCombiner = null;
            return logHolderCombiner;
        }
        return null;
    }
}
