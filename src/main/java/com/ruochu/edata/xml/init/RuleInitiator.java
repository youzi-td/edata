package com.ruochu.edata.xml.init;

import com.ruochu.edata.constant.Constants;
import com.ruochu.edata.enums.RuleTypeEnum;
import com.ruochu.edata.exception.XmlConfigException;
import com.ruochu.edata.util.EmptyChecker;
import com.ruochu.edata.xml.Rule;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 规则Initiator
 *
 * @author RanPengCheng
 * @date 2019/7/10 12:40
 */
public class RuleInitiator {

    private List<Rule> rules;
    private Rule currentRule;
    private String cellTitle;
    private static final Map<String, String> ERROR_MSG_MAP = new HashMap<>();

    RuleInitiator(List<Rule> rules, String cellTitle) {
        this.rules = rules;
        this.cellTitle = cellTitle;
    }

    void init() {
        for (Rule rule : rules) {
            this.currentRule = rule;
            // 属性检查
            checkAttr();
            // 自定义校验初始化
            initExpressionAndValues();
            // 错误信息
            initErrorMsg();
        }
    }

    private void initErrorMsg() {
        String type = currentRule.getType();
        String errorMsg = currentRule.getErrorMsg();
        String expression = currentRule.getXmlExpression();
        if (EmptyChecker.isEmpty(errorMsg)) {
            String errorMsgKey = type + expression;

            if (ERROR_MSG_MAP.containsKey(errorMsgKey)) {
                errorMsg = ERROR_MSG_MAP.get(errorMsgKey);
            } else {
                if (RuleTypeEnum.NUMBER.getType().equals(type)) {
                    String[] arr = expression.split(Constants.SEPARATOR);
                    if ("0".equals(arr[1])) {
                        errorMsg = "整数，位数不超过" + arr[0] + "位";
                    } else {
                        errorMsg = String.format(Objects.requireNonNull(RuleTypeEnum.defaultErrorMsg(type)), arr[0], arr[1]);
                    }
                } else  {
                    errorMsg = String.format(Objects.requireNonNull(RuleTypeEnum.defaultErrorMsg(type)), expression);
                }

                ERROR_MSG_MAP.put(errorMsgKey, errorMsg);
            }
            currentRule.setErrorMsg(errorMsg);
        }

    }

    private void initExpressionAndValues() {
        String values = currentRule.getXmlValues();
        if (RuleTypeEnum.CUSTOM.getType().equals(currentRule.getType())) {
            if (EmptyChecker.notEmpty(values)){
                String[] valueArr = values.split(Constants.SEPARATOR);
                Map<String, String> customValuesMap = new HashMap<>(valueArr.length);
                for (String valExp : valueArr) {
                    valExp = valExp.trim();
                    if (!valExp.startsWith(Constants.OPENT) || !valExp.endsWith(Constants.CLOSE)) {
                        throw new XmlConfigException("自定义校验的values传值不合规范，格式应该为[${字段名},${字段名}···]！cell:" + cellTitle);
                    }

                    customValuesMap.put(StringUtils.substringBetween(valExp, Constants.OPENT, Constants.CLOSE), "");
                }

                currentRule.setCustomValuesMap(customValuesMap);
            }
            return;
        }

        String expression = currentRule.getXmlExpression();
        dealFields(expression, Type.EXPRESSION);
        dealFields(values, Type.VALUES);

    }

    private void dealFields(String expression, Type type) {
        if (EmptyChecker.isEmpty(expression)){
            return;
        }
        String[] expArr = StringUtils.substringsBetween(expression, Constants.OPENT, Constants.CLOSE);
        List<String> fields = null;
        String newExp = expression;
        if (EmptyChecker.notEmpty(expArr)){
            fields = new ArrayList<>(expArr.length);
            for (String field : expArr){
                fields.add(field.trim());
                newExp = newExp.replaceAll("\\$\\{" + field.trim() + "}", "%s");
            }
        }

        switch (type){
            case VALUES:
                currentRule.setValues(newExp);
                currentRule.setValFields(fields);
                break;
            case EXPRESSION:
                currentRule.setExpression(newExp);
                currentRule.setExpFields(fields);
                break;
            default: break;
        }
    }

    private void checkAttr() {

        String type = currentRule.getType();
        String expression = currentRule.getXmlExpression();
        String values = currentRule.getXmlValues();

        if (EmptyChecker.isEmpty(type)) {
            throw new XmlConfigException("rule的type不能为空！cell:" + cellTitle);
        }
        if (!RuleTypeEnum.exist(type)) {
            throw new XmlConfigException("rule的type[" + type + "]不合法！cell" + cellTitle);
        }

        if (RuleTypeEnum.DATE.getType().equals(type) && EmptyChecker.isEmpty(expression)) {
            currentRule.setXmlExpression(Constants.DEFAULT_DATE_FORMAT);
        }

        if (RuleTypeEnum.NUMBER.getType().equals(type)) {
            if (EmptyChecker.isEmpty(expression) || !expression.matches(Constants.NUMBER_FORMAT)) {
                throw new XmlConfigException("rule为number类型的校验时,expression为必填项，表示数字格式，格式为[整数位,小数位]！cell:" + cellTitle);
            }
        } else if (RuleTypeEnum.MAX_LENGTH.getType().equals(type)) {
            if (EmptyChecker.isEmpty(expression) || !expression.matches(Constants.LENGTH_FORMAT)) {
                throw new XmlConfigException("rule为maxLength类型的检验时，expression为必填项，表示长度限制，格式为[整数]！cell:" + cellTitle);
            }
        } else if (RuleTypeEnum.CUSTOM.getType().equals(type)) {
            if (EmptyChecker.isEmpty(expression)) {
                throw new XmlConfigException("rule为custom类型的检验时，expression为必填项，表示自定义校验器的实现类，格式为[全类名]！cell:" + cellTitle);
            }
//            if (EmptyChecker.isEmpty(values)) {
//                throw new XmlConfigException("rule为custom类型的检验时，value为必填项，表示需要向自定校验器传递的值，格式为[${字段名}, ${字段名}...]！cell:" + cellTitle);
//            }
        } else if (RuleTypeEnum.BOOLEAN.getType().equals(type)) {
            if (EmptyChecker.isEmpty(expression)) {
                throw new XmlConfigException("rule为boolean类型的检验时，expression为必填项，表示布尔表达式，格式为[布尔表达式]！cell:" + cellTitle);
            }
        } else if (RuleTypeEnum.REGEX.getType().equals(type)) {
            if (EmptyChecker.isEmpty(expression)) {
                throw new XmlConfigException("rule为regex类型的检验时，expression为必填项，表示正则表达式，格式为[正则表达式]！cell:" + cellTitle);
            }
        } else if (RuleTypeEnum.SELECTION.getType().equals(type)) {
            if (EmptyChecker.isEmpty(values)) {
                throw new XmlConfigException("rule为selection类型的检验时，value为必填项，表示单选范围，格式为[值,值···]！cell:" + cellTitle);
            }
        } else if (RuleTypeEnum.SELECTIONS.getType().equals(type)) {
            if (EmptyChecker.isEmpty(values)) {
                throw new XmlConfigException("rule为selection类型的检验时，value为必填项，表示多选范围，格式为[值,值···]！cell:" + cellTitle);
            }
        }

    }

    enum Type{
        EXPRESSION,
        VALUES
    }
}


