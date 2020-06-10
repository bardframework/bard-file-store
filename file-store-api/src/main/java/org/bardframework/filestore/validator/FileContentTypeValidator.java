package org.bardframework.filestore.validator;

import org.bardframework.commons.utils.AssertionUtils;
import org.bardframework.filestore.file.FileInfo;
import org.bardframework.validator.FieldValueHolder;
import org.bardframework.validator.field.SingleFieldValidatorAbstract;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by v.zafari on 30/01/2016.
 */
public class FileContentTypeValidator extends SingleFieldValidatorAbstract<FileInfo> {

    protected final Set<String> validContentTypes;

    public FileContentTypeValidator(Set<String> validContentTypes) {
        this(validContentTypes, "file.content_type_error");
    }

    public FileContentTypeValidator(Set<String> validContentTypes, String errorCode) {
        super(errorCode);
        AssertionUtils.notEmpty(validContentTypes, "null content type not acceptable");
        this.validContentTypes = validContentTypes;
    }

    @Override
    protected boolean isValid(FileInfo value) {
        return validContentTypes.contains(value.getContentType());
    }

    @Override
    protected List<Object> getArgs(FieldValueHolder<FileInfo> fieldValue) {
        return Arrays.asList(fieldValue.translateFieldName(messageSource, getLocale()), validContentTypes.toString());
    }
}
