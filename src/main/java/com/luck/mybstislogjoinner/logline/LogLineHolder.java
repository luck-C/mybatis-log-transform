package com.luck.mybstislogjoinner.logline;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LogLineHolder {

    private final String statementLine;
    private String parameterLine;

    public LogLineHolder(String statementLine, String parameterLine) {
        this.statementLine = statementLine;
        this.parameterLine = parameterLine;
    }


    public static LogLineHolder valueOf(String statementLine){
        return new LogLineHolder(statementLine,null);
    }

    public List<String> getParameter(){
        if(Objects.isNull(parameterLine)){
            return Collections.emptyList();
        }
        return LogLineParser.parseParameter(parameterLine);
    }

    public String formattedSqlStatement(){
        String sql = LogLineParser.formatStatement(statementLine, getParameter());
        if(!sql.endsWith(";")){
            return sql + ";\n";
        }
        return sql + "\n";
    }

    public String getStatementLine() {
        return statementLine;
    }


    public void setParameterLine(String parameterLine) {
        this.parameterLine = parameterLine;
    }

}
