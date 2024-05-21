package org.mifos.pheebillpay.validators;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.mifos.connector.common.validation.ValidationCodeType;
import org.mifos.connector.common.validation.ValidatorBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UnsupportedParameterValidator {

    @Value("#{'${default_api_headers}'.split(',')}")
    private Set<String> defaultHeader;

    final StringBuilder validationErrorCode = new StringBuilder("error.msg.parameter.unsupported");

    public void handleUnsupportedParameterValidation(Map<String, Object> additionalProperties, ValidatorBuilder validatorBuilder) {

        for (final String parameterName : additionalProperties.keySet()) {
            final StringBuilder defaultEnglishMessage = new StringBuilder("The parameter ").append(parameterName)
                    .append(" is not supported.");

            ValidationCodeType validationCode = convertToStructuredErrorMessage(validationErrorCode.toString(),
                    defaultEnglishMessage.toString());
            validatorBuilder.failWithCode(validationCode);
        }

    }

    public void handleRequiredParameterValidation(List<String> fields, Set<String> requiredFields, ValidatorBuilder validatorBuilder) {

        for (final String fieldName : fields) {
            if (!fieldName.equals("additionalProperties") && !requiredFields.contains(fieldName) && !defaultHeader.contains(fieldName)) {

                final StringBuilder defaultEnglishMessage = new StringBuilder("The parameter ").append(fieldName)
                        .append(" is not supported.");

                ValidationCodeType validationCode = convertToStructuredErrorMessage(validationErrorCode.toString(),
                        defaultEnglishMessage.toString());
                validatorBuilder.failWithCode(validationCode);
            }
        }

    }

    private ValidationCodeType convertToStructuredErrorMessage(String errorCode, String errorMessage) {
        return new ValidationCodeType() {

            @Override
            public String getCode() {
                return errorCode;
            }

            @Override
            public String getCategory() {
                return "Validation";
            }

            @Override
            public String getMessage() {
                return errorMessage;
            }
        };
    }

}
