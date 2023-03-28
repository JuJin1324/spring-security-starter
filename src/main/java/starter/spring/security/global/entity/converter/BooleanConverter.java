package starter.spring.security.global.entity.converter;

import org.springframework.util.ObjectUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by Yoo Ju Jin(jujin@100fac.com)
 * Created Date : 2021/11/05
 * Copyright (C) 2021, Centum Factorial all rights reserved.
 */

@Converter
public class BooleanConverter implements AttributeConverter<Boolean, Character> {
    @Override
    public Character convertToDatabaseColumn(Boolean entityField) {
        if (ObjectUtils.isEmpty(entityField)) {
            return null;
        }
        return Boolean.TRUE.equals(entityField) ? 'Y' : 'N';
    }

    @Override
    public Boolean convertToEntityAttribute(Character dbColumn) {
        if (ObjectUtils.isEmpty(dbColumn)) {
            return null;
        }
        return dbColumn.equals('Y');
    }
}
