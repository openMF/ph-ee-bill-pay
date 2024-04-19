package org.mifos.pheebillpay.validators;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mifos.connector.common.validation.ValidationCodeType;
import org.mifos.connector.common.validation.ValidatorBuilder;
import org.springframework.stereotype.Component;

@Component
public class UnsupportedParameterValidator {

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
            if (fieldName != "additionalProperties" && !requiredFields.contains(fieldName)) {

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
