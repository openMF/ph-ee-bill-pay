package org.mifos.pheebillpay.validators;

import static org.mifos.connector.common.exception.PaymentHubError.ExtValidationError;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.mifos.connector.common.channel.dto.PhErrorDTO;
import org.mifos.connector.common.exception.PaymentHubErrorCategory;
import org.mifos.connector.common.validation.ValidatorBuilder;
import org.mifos.pheebillpay.service.HeaderBuilderService;
import org.mifos.pheebillpay.utils.BillValidatorEnum;
import org.mifos.pheebillpay.utils.HeaderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HeaderValidator {

    @Autowired
    HeaderBuilderService headerBuilderService;
    @Autowired
    UnsupportedParameterValidator unsupportedParameterValidator;

    private static final String resource = "billPayValidator";

    public PhErrorDTO validateBillInquiryRequest(HttpServletRequest request) {
        final ValidatorBuilder validatorBuilder = new ValidatorBuilder();

        List<String> headers = getHeaderList(request);
        Set<String> requiredHeaders = headerBuilderService.buildHeadersForBillInquiryAPI();

        // Checks for unsupported parameters
        unsupportedParameterValidator.handleRequiredParameterValidation(headers, requiredHeaders, validatorBuilder);

        // Checks for X-CorrelationID
        validatorBuilder.reset().resource(resource).parameter(HeaderConstants.X_CORRELATION_ID)
                .value(request.getHeader(HeaderConstants.X_CORRELATION_ID)).isNullWithFailureCode(BillValidatorEnum.INVALID_CORRELATION_ID)
                .validateFieldMaxLengthWithFailureCodeAndErrorParams(20, BillValidatorEnum.INVALID_CORRELATION_ID_LENGTH);

        // Checks for Platform-TenantId
        validatorBuilder.reset().resource(resource).parameter(HeaderConstants.PLATFORM_TENANT_ID)
                .value(request.getHeader(HeaderConstants.PLATFORM_TENANT_ID))
                .isNullWithFailureCode(BillValidatorEnum.INVALID_PLATFORM_TENANT_ID)
                .validateFieldMaxLengthWithFailureCodeAndErrorParams(20, BillValidatorEnum.INVALID_PLATFORM_TENANT_ID_LENGTH);

        // Checks for X-Callback-URL
        validatorBuilder.reset().resource(resource).parameter(HeaderConstants.X_CALLBACK_URL)
                .value(request.getHeader(HeaderConstants.X_CALLBACK_URL)).isNullWithFailureCode(BillValidatorEnum.INVALID_CALLBACK_URL)
                .validateFieldMaxLengthWithFailureCodeAndErrorParams(100, BillValidatorEnum.INVALID_CALLBACK_URL_LENGTH);

        // Checks for Payer-FSP-Id
        validatorBuilder.reset().resource(resource).parameter(HeaderConstants.PAYER_FSP_ID)
                .value(request.getHeader(HeaderConstants.PAYER_FSP_ID)).isNullWithFailureCode(BillValidatorEnum.INVALID_PAYER_FSP_ID)
                .validateFieldMaxLengthWithFailureCodeAndErrorParams(20, BillValidatorEnum.INVALID_PAYER_FSP_ID_LENGTH);

        return handleValidationErrors(validatorBuilder);

    }

    public PhErrorDTO validateBillPaymentRequest(HttpServletRequest request) {
        final ValidatorBuilder validatorBuilder = new ValidatorBuilder();

        List<String> headers = getHeaderList(request);
        Set<String> requiredHeaders = headerBuilderService.buildHeadersForBillPaymentsAPI();

        // Checks for unsupported parameters
        unsupportedParameterValidator.handleRequiredParameterValidation(headers, requiredHeaders, validatorBuilder);

        // Checks for X-CorrelationID
        validatorBuilder.reset().resource(resource).parameter(HeaderConstants.X_CORRELATION_ID)
                .value(request.getHeader(HeaderConstants.X_CORRELATION_ID)).isNullWithFailureCode(BillValidatorEnum.INVALID_CORRELATION_ID)
                .validateFieldMaxLengthWithFailureCodeAndErrorParams(20, BillValidatorEnum.INVALID_CORRELATION_ID_LENGTH);

        // Checks for X-Platform-TenantId
        validatorBuilder.reset().resource(resource).parameter(HeaderConstants.X_PLATFORM_TENANT_ID)
                .value(request.getHeader(HeaderConstants.X_PLATFORM_TENANT_ID))
                .isNullWithFailureCode(BillValidatorEnum.INVALID_PLATFORM_TENANT_ID)
                .validateFieldMaxLengthWithFailureCodeAndErrorParams(20, BillValidatorEnum.INVALID_PLATFORM_TENANT_ID_LENGTH);

        // Checks for X-Callback-URL
        validatorBuilder.reset().resource(resource).parameter(HeaderConstants.X_CALLBACK_URL)
                .value(request.getHeader(HeaderConstants.X_CALLBACK_URL)).isNullWithFailureCode(BillValidatorEnum.INVALID_CALLBACK_URL)
                .validateFieldMaxLengthWithFailureCodeAndErrorParams(100, BillValidatorEnum.INVALID_CALLBACK_URL_LENGTH);

        // Checks for X-PayerFSP-Id
        validatorBuilder.reset().resource(resource).parameter(HeaderConstants.X_PAYER_FSP_ID)
                .value(request.getHeader(HeaderConstants.X_PAYER_FSP_ID)).isNullWithFailureCode(BillValidatorEnum.INVALID_PAYER_FSP_ID)
                .validateFieldMaxLengthWithFailureCodeAndErrorParams(20, BillValidatorEnum.INVALID_PAYER_FSP_ID_LENGTH);

        return handleValidationErrors(validatorBuilder);

    }

    public PhErrorDTO validateBillRTPRequest(HttpServletRequest request) {
        final ValidatorBuilder validatorBuilder = new ValidatorBuilder();

        List<String> headers = getHeaderList(request);
        Set<String> requiredHeaders = headerBuilderService.buildHeadersForBillRtpReqAPI();

        // Checks for unsupported parameters
        unsupportedParameterValidator.handleRequiredParameterValidation(headers, requiredHeaders, validatorBuilder);

        // Checks for X-Client-Correlation-ID
        validatorBuilder.reset().resource(resource).parameter(HeaderConstants.X_CLIENT_CORRELATION_ID)
                .value(request.getHeader(HeaderConstants.X_CLIENT_CORRELATION_ID))
                .isNullWithFailureCode(BillValidatorEnum.INVALID_CORRELATION_ID)
                .validateFieldMaxLengthWithFailureCodeAndErrorParams(20, BillValidatorEnum.INVALID_CORRELATION_ID_LENGTH);

        // Checks for X-Platform-TenantId
        validatorBuilder.reset().resource(resource).parameter(HeaderConstants.X_PLATFORM_TENANT_ID)
                .value(request.getHeader(HeaderConstants.X_PLATFORM_TENANT_ID)).isNullWithFailureCode(BillValidatorEnum.INVALID_TENANT_ID)
                .validateFieldMaxLengthWithFailureCodeAndErrorParams(20, BillValidatorEnum.INVALID_TENANT_ID_LENGTH);

        // Checks for X-Biller-Id
        validatorBuilder.reset().resource(resource).parameter(HeaderConstants.X_BILLER_ID)
                .value(request.getHeader(HeaderConstants.X_BILLER_ID)).isNullWithFailureCode(BillValidatorEnum.INVALID_BILLER_ID)
                .validateFieldMaxLengthWithFailureCodeAndErrorParams(20, BillValidatorEnum.INVALID_BILLER_ID_LENGTH);

        // Checks for X-Callback-URL
        validatorBuilder.reset().resource(resource).parameter(HeaderConstants.X_CALLBACK_URL)
                .value(request.getHeader(HeaderConstants.X_CALLBACK_URL)).isNullWithFailureCode(BillValidatorEnum.INVALID_CALLBACK_URL)
                .validateFieldMaxLengthWithFailureCodeAndErrorParams(100, BillValidatorEnum.INVALID_CALLBACK_URL_LENGTH);

        // Checks for X-Registering-Institution-ID
        validatorBuilder.reset().resource(resource).parameter(HeaderConstants.X_REGISTERING_INSTITUTION_ID)
                .value(request.getHeader(HeaderConstants.X_REGISTERING_INSTITUTION_ID)).ignoreIfNull()
                .validateFieldMaxLengthWithFailureCodeAndErrorParams(100, BillValidatorEnum.INVALID_REGISTERING_INSTITUTION_ID_LENGTH);

        return handleValidationErrors(validatorBuilder);
    }

    private PhErrorDTO handleValidationErrors(ValidatorBuilder validatorBuilder) {
        if (validatorBuilder.hasError()) {
            validatorBuilder.errorCategory(PaymentHubErrorCategory.Validation.toString())
                    .errorCode(BillValidatorEnum.HEADER_VALIDATION_ERROR.getCode())
                    .errorDescription(BillValidatorEnum.HEADER_VALIDATION_ERROR.getMessage())
                    .developerMessage(BillValidatorEnum.HEADER_VALIDATION_ERROR.getMessage())
                    .defaultUserMessage(BillValidatorEnum.HEADER_VALIDATION_ERROR.getMessage());

            PhErrorDTO.PhErrorDTOBuilder phErrorDTOBuilder = new PhErrorDTO.PhErrorDTOBuilder(ExtValidationError.getErrorCode());
            phErrorDTOBuilder.fromValidatorBuilder(validatorBuilder);
            return phErrorDTOBuilder.build();
        }
        return null;
    }

    public List<String> getHeaderList(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaderNames();
        return Collections.list(request.getHeaderNames());
    }
}
