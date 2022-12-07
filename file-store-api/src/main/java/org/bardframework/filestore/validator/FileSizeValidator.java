package org.bardframework.filestore.validator;

import org.bardframework.filestore.file.FileInfo;
import org.bardframework.validator.FieldValueHolder;
import org.bardframework.validator.field.SingleFieldValidatorAbstract;

import java.util.List;

/**
 * Created by v.zafari on 30/01/2016.
 */
public class FileSizeValidator extends SingleFieldValidatorAbstract<FileInfo> {

    /**
     * min size according byte
     */
    protected final long minSize;
    /**
     * max size according byte
     */
    protected final long maxSize;

    public FileSizeValidator(long minSize, long maxSize) {
        this(minSize, maxSize, "file.size_error");
    }

    public FileSizeValidator(long minSize, long maxSize, String errorCode) {
        super(errorCode);
        this.minSize = minSize;
        this.maxSize = maxSize;
    }

    @Override
    protected boolean isValid(FileInfo value) {
        return value.getSize() > minSize && value.getSize() < maxSize;
    }

    @Override
    protected List<Object> getArgs(FieldValueHolder<FileInfo> fieldValue) {
        return List.of(fieldValue.translateFieldName(messageSource, getLocale()), null, null);
        //FIXME BARD
//                UtilityMethods.translateByteToKMGT(minSize, messageSource, LocaleContextHolder.getLocale()),
//                UtilityMethods.translateByteToKMGT(maxSize, messageSource, LocaleContextHolder.getLocale()));
    }
}
