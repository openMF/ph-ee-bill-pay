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
import org.mifos.pheebillpay.utils.BillValidatorEnum;
import org.mifos.pheebillpay.utils.HeaderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HeaderValidator {

    @Autowired
    UnsupportedParameterValidator unsupportedParameterValidator;

    @Value("#{'${default_api_headers}'.split(',')}")
    private List<String> defaultHeader;

    private static final String resource = "billPayValidator";

    public PhErrorDTO validateBillInquiryRequest(Set<String> requiredHeaders, HttpServletRequest request) {
        final ValidatorBuilder validatorBuilder = new ValidatorBuilder();

        List<String> headers = getHeaderList(request);
        // Checks for unsupported parameters
        unsupportedParameterValidator.handleRequiredParameterValidation(headers, requiredHeaders, validatorBuilder);

        // Checks for X-CorrelationID
        validatorBuilder.validateFieldIgnoreNullAndMaxLengthWithFailureCode(resource, HeaderConstants.X_CORRELATION_ID,
                request.getHeader(HeaderConstants.X_CORRELATION_ID), 20, BillValidatorEnum.INVALID_CORRELATION_ID_LENGTH);

        // Checks for Platform-TenantId
        validatorBuilder.validateFieldIgnoreNullAndMaxLengthWithFailureCode(resource, HeaderConstants.PLATFORM_TENANT_ID,
                request.getHeader(HeaderConstants.PLATFORM_TENANT_ID), 20, BillValidatorEnum.INVALID_PLATFORM_TENANT_ID_LENGTH);

        // Checks for X-Callback-URL
        validatorBuilder.validateFieldIgnoreNullAndMaxLengthWithFailureCode(resource, HeaderConstants.X_CALLBACKURL,
                request.getHeader(HeaderConstants.X_CALLBACKURL), 100, BillValidatorEnum.INVALID_CALLBACK_URL_LENGTH);

        // Checks for Payer-FSP-Id
        validatorBuilder.validateFieldIgnoreNullAndMaxLengthWithFailureCode(resource, HeaderConstants.PAYER_FSP_ID,
                request.getHeader(HeaderConstants.PAYER_FSP_ID), 20, BillValidatorEnum.INVALID_PAYER_FSP_ID_LENGTH);

        return handleValidationErrors(validatorBuilder);

    }

    public PhErrorDTO validateBillPaymentRequest(Set<String> requiredHeaders, HttpServletRequest request) {
        final ValidatorBuilder validatorBuilder = new ValidatorBuilder();

        List<String> headers = getHeaderList(request);
        // Set<String> requiredHeaders = headerBuilderService.buildHeadersForBillPaymentsAPI();

        // Checks for unsupported parameters
        unsupportedParameterValidator.handleRequiredParameterValidation(headers, requiredHeaders, validatorBuilder);

        // Checks for X-CorrelationID
        validatorBuilder.validateFieldIsNullAndMaxLengthWithFailureCode(resource, HeaderConstants.X_CORRELATION_ID,
                request.getHeader(HeaderConstants.X_CORRELATION_ID), BillValidatorEnum.INVALID_CORRELATION_ID, 20,
                BillValidatorEnum.INVALID_CORRELATION_ID_LENGTH);

        // Checks for X-Platform-TenantId
        validatorBuilder.validateFieldIsNullAndMaxLengthWithFailureCode(resource, HeaderConstants.X_PLATFORM_TENANT_ID,
                request.getHeader(HeaderConstants.X_PLATFORM_TENANT_ID), BillValidatorEnum.INVALID_PLATFORM_TENANT_ID, 20,
                BillValidatorEnum.INVALID_PLATFORM_TENANT_ID_LENGTH);

        // Checks for X-Callback-URL
        validatorBuilder.validateFieldIsNullAndMaxLengthWithFailureCode(resource, HeaderConstants.X_CALLBACKURL,
                request.getHeader(HeaderConstants.X_CALLBACKURL), BillValidatorEnum.INVALID_CALLBACK_URL, 100,
                BillValidatorEnum.INVALID_CALLBACK_URL_LENGTH);

        // Checks for X-PayerFSP-Id
        validatorBuilder.validateFieldIsNullAndMaxLengthWithFailureCode(resource, HeaderConstants.X_PAYER_FSP_ID,
                request.getHeader(HeaderConstants.X_PAYER_FSP_ID), BillValidatorEnum.INVALID_PAYER_FSP_ID, 20,
                BillValidatorEnum.INVALID_PAYER_FSP_ID_LENGTH);
        return handleValidationErrors(validatorBuilder);

    }

    public PhErrorDTO validateBillRTPRequest(Set<String> requiredHeaders, HttpServletRequest request) {
        final ValidatorBuilder validatorBuilder = new ValidatorBuilder();

        List<String> headers = getHeaderList(request);

        // Checks for unsupported parameters
        unsupportedParameterValidator.handleRequiredParameterValidation(headers, requiredHeaders, validatorBuilder);

        // Checks for X-Client-Correlation-ID
        validatorBuilder.validateFieldIsNullAndMaxLengthWithFailureCode(resource, HeaderConstants.X_CLIENT_CORRELATION_ID,
                request.getHeader(HeaderConstants.X_CLIENT_CORRELATION_ID), BillValidatorEnum.INVALID_CORRELATION_ID, 20,
                BillValidatorEnum.INVALID_CORRELATION_ID_LENGTH);

        // Checks for X-Platform-TenantId
        validatorBuilder.validateFieldIsNullAndMaxLengthWithFailureCode(resource, HeaderConstants.X_PLATFORM_TENANT_ID,
                request.getHeader(HeaderConstants.X_PLATFORM_TENANT_ID), BillValidatorEnum.INVALID_TENANT_ID, 20,
                BillValidatorEnum.INVALID_TENANT_ID_LENGTH);

        // Checks for X-Biller-Id
        validatorBuilder.validateFieldIsNullAndMaxLengthWithFailureCode(resource, HeaderConstants.X_BILLER_ID,
                request.getHeader(HeaderConstants.X_BILLER_ID), BillValidatorEnum.INVALID_BILLER_ID, 20,
                BillValidatorEnum.INVALID_BILLER_ID_LENGTH);

        // Checks for X-Callback-URL
        validatorBuilder.validateFieldIsNullAndMaxLengthWithFailureCode(resource, HeaderConstants.X_CALLBACK_URL,
                request.getHeader(HeaderConstants.X_CALLBACK_URL), BillValidatorEnum.INVALID_CALLBACK_URL, 100,
                BillValidatorEnum.INVALID_CALLBACK_URL_LENGTH);

        // Checks for X-Registering-Institution-ID
        validatorBuilder.validateFieldIgnoreNullAndMaxLengthWithFailureCode(resource, HeaderConstants.X_REGISTERING_INSTITUTION_ID,
                request.getHeader(HeaderConstants.X_REGISTERING_INSTITUTION_ID), 100,
                BillValidatorEnum.INVALID_REGISTERING_INSTITUTION_ID_LENGTH);
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
