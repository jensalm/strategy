package com.captechventures.strategy;

import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

public class StrategySelectorEvaluator {

    private static final SpelParserConfiguration parserConfiguration = new SpelParserConfiguration(true, true);

    private static final SpelExpressionParser expressionParser = new SpelExpressionParser(parserConfiguration);

    private static final TemplateParserContext parserContext = new TemplateParserContext("#{", "}");

    private final StandardEvaluationContext sec;

    public StrategySelectorEvaluator(Map<String, Object> context) {
        sec = new StandardEvaluationContext();
        sec.setVariables(context);
    }

    public Boolean getSelector(String expression) {
        Expression compiledExpression = expressionParser.parseExpression(expression, parserContext);
        return compiledExpression.getValue(sec, Boolean.class);
    }

}
