package org.bardframework.filestore.validator;

import org.bardframework.commons.utils.Fraction;
import org.bardframework.filestore.file.FileInfo;
import org.bardframework.validator.FieldValueHolder;
import org.bardframework.validator.field.SingleFieldValidatorAbstract;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by v.zafari on 30/01/2016.
 */
public class PictureAspectRatioValidator extends SingleFieldValidatorAbstract<FileInfo> {

    protected final double aspectRatio;
    protected final double tolerance;

    public PictureAspectRatioValidator(double aspectRatio, double tolerance) {
        this(aspectRatio, tolerance, "picture.aspect_ratio.error");
    }

    public PictureAspectRatioValidator(double aspectRatio, double tolerance, String errorCode) {
        super(errorCode);
        this.aspectRatio = aspectRatio;
        if (tolerance < 0.00 || tolerance > 0.99) {
            throw new IllegalArgumentException("tolerance must between 0.00 and 0.99");
        }
        this.tolerance = tolerance;
    }

    @Override
    protected boolean isValid(FileInfo value) {
        BufferedImage image;
        try {
            image = ImageIO.read(new ByteArrayInputStream(value.getBytes()));
        } catch (Exception e) {
            LOGGER.error("error reading image from multi part file. {}", value);
            throw new IllegalArgumentException(e);
        }
        double minAspectRatio = aspectRatio - (aspectRatio * 0.1);
        double maxAspectRatio = aspectRatio + (aspectRatio * 0.1);
        double pictureAspectRatio = (double) image.getWidth() / (double) image.getHeight();

        return pictureAspectRatio < minAspectRatio || pictureAspectRatio > maxAspectRatio;
    }

    @Override
    protected List<Object> getArgs(FieldValueHolder<FileInfo> fieldValue) {
        Fraction fraction = Fraction.getFraction(aspectRatio);
        return List.of(fieldValue.translateFieldName(messageSource, getLocale()), fraction.getNumerator(), fraction.getDenominator());
    }
}
