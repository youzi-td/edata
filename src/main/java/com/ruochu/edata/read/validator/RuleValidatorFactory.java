package com.ruochu.edata.read.validator;


import com.ruochu.edata.enums.RuleTypeEnum;
import com.ruochu.edata.exception.ERuntimeException;
import com.ruochu.edata.read.validator.impl.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 校验器工厂
 *
 * @author RanPengCheng
 * @date 2019/7/14 18:10
 */
public class RuleValidatorFactory {

    private RuleValidatorFactory() {
    }

    private static final Map<String, IRuleValidator> VALIDATOR_MAP = new HashMap<>(16);

    public static IRuleValidator getValidator(String type) {
        if (VALIDATOR_MAP.containsKey(type)){
            return VALIDATOR_MAP.get(type);
        }

        IRuleValidator validator;

        if (RuleTypeEnum.DATE.getType().equals(type)) {
            validator = new DateValidator();
        } else if (RuleTypeEnum.NUMBER.getType().equals(type)) {
            validator = new NumberValidator();
        } else if (RuleTypeEnum.MAX_LENGTH.getType().equals(type)) {
            validator = new LengthValidator();
        } else if (RuleTypeEnum.CUSTOM.getType().equals(type)) {
            validator = new CustomValidator();
        } else if (RuleTypeEnum.BOOLEAN.getType().equals(type)) {
            validator = new BooleanValidator();
        } else if (RuleTypeEnum.REGEX.getType().equals(type)) {
            validator = new RegexValidator();
        } else if (RuleTypeEnum.SELECTION.getType().equals(type)) {
            validator = new SelectionValidator();
        } else if (RuleTypeEnum.SELECTIONS.getType().equals(type)) {
            validator = new SelectionsValidator();
        } else if (RuleTypeEnum.REQUIRED.getType().equals(type)) {
            validator = new RequiredValidator();
        } else if (RuleTypeEnum.BLANK.getType().equals(type)) {
            validator = new BlankValidator();
        } else if (RuleTypeEnum.UNIQUE.getType().equals(type)) {
            validator = new UniqueValidator();
        } else {
          throw new ERuntimeException("未找到type[" + type + "]类型的校验器！");
        }

        VALIDATOR_MAP.put(type, validator);
        return validator;
    }


    /**
     * 清理校验器里的缓存
     */
    public static void clearValidator() {
        if (!VALIDATOR_MAP.isEmpty()) {
            for (IRuleValidator validator : VALIDATOR_MAP.values()) {
                validator.clear();
            }
        }
    }

}
