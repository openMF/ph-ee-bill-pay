package org.mifos.pheebillpay.validators;

import static org.mifos.connector.common.exception.PaymentHubError.ExtValidationError;

import java.util.Objects;
import org.mifos.connector.common.channel.dto.PhErrorDTO;
import org.mifos.connector.common.exception.PaymentHubErrorCategory;
import org.mifos.connector.common.validation.ValidatorBuilder;
import org.mifos.pheebillpay.data.Alias;
import org.mifos.pheebillpay.data.Bill;
import org.mifos.pheebillpay.data.BillPaymentsReqDTO;
import org.mifos.pheebillpay.data.BillRTPReqDTO;
import org.mifos.pheebillpay.data.PayerFSPDetail;
import org.mifos.pheebillpay.utils.BillPayDTOConstant;
import org.mifos.pheebillpay.utils.BillValidatorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BillPayValidator {

    @Autowired
    UnsupportedParameterValidator unsupportedParameterValidator;

    private static final String resource = "billPayValidator";

    public static final int expectedRequestIdLength = 12;
    public static final int expectedBillInquiryRequestIdLength = 12;
    public static final int expectedBillIdLength = 20;
    public static final int expectedPaymentRefereneIDLength = 16;
    public static final int expectedClientCorrelationIdLength = 12;
    public static final int expectedBillerIdLength = 10;
    public static final int expectedRequestTypeLength = 2;
    public static final int expectedPayerFSPIDLength = 10;
    public static final int expectedFinancialAddressLength = 30;
    public static final int expectedAliasTypeLength = 2;
    public static final int expectedAliasIdLength = 30;
    public static final int expectedBillerNameLength = 10;

    public PhErrorDTO validateBillPayments(BillPaymentsReqDTO request) {
        final ValidatorBuilder validatorBuilder = new ValidatorBuilder();

        // Checks for unsupportedParameter
        unsupportedParameterValidator.handleUnsupportedParameterValidation(request.getAdditionalProperties(), validatorBuilder);

        // Checks for billInquiryRequestId
        validatorBuilder.reset().resource(resource).parameter(BillPayDTOConstant.billInquiryRequestId)
                .value(request.getBillInquiryRequestId()).ignoreIfNull().validateFieldMaxLengthWithFailureCodeAndErrorParams(
                        expectedBillInquiryRequestIdLength, BillValidatorEnum.INVALID_BILL_INQUIRY_REQUEST_ID_LENGTH);

        // Checks for billId
        validatorBuilder.reset().resource(resource).parameter(BillPayDTOConstant.billId).value(request.getBillId())
                .isNullWithFailureCode(BillValidatorEnum.INVALID_BILL_ID)
                .validateFieldMaxLengthWithFailureCodeAndErrorParams(expectedBillIdLength, BillValidatorEnum.INVALID_BILL_ID_LENGTH);

        // Checks for paymentReferenceID
        validatorBuilder.reset().resource(resource).parameter(BillPayDTOConstant.paymentRefereneID).value(request.getPaymentReferenceID())
                .isNullWithFailureCode(BillValidatorEnum.INVALID_PAYMENT_REFERENCE_ID).validateFieldMaxLengthWithFailureCodeAndErrorParams(
                        expectedPaymentRefereneIDLength, BillValidatorEnum.INVALID_PAYMENT_REFERENCE_ID_LENGTH);

        return handleValidationErrors(validatorBuilder);
    }

    public PhErrorDTO validateBillRTPRequest(BillRTPReqDTO request) {
        final ValidatorBuilder validatorBuilder = new ValidatorBuilder();

        // Checks for unsupportedParameter
        unsupportedParameterValidator.handleUnsupportedParameterValidation(request.getAdditionalProperties(), validatorBuilder);

        // Checks for clientCorrelationId
        validatorBuilder.reset().resource(resource).parameter(BillPayDTOConstant.clientCorrelationId)
                .value(request.getClientCorrelationId()).ignoreIfNull().validateFieldMaxLengthWithFailureCodeAndErrorParams(
                        expectedClientCorrelationIdLength, BillValidatorEnum.INVALID_CLIENT_CORRELATION_ID_LENGTH);

        // Checks for billID/billerID
        validatorBuilder.reset().resource(resource).parameter(BillPayDTOConstant.billID).value(request.getBillID())
                .isNullWithFailureCode(BillValidatorEnum.INVALID_BILL_ID)
                .validateFieldMaxLengthWithFailureCodeAndErrorParams(expectedBillerIdLength, BillValidatorEnum.INVALID_BILL_ID_LENGTH);

        // Checks for requestType
        validatorBuilder.reset().resource(resource).parameter(BillPayDTOConstant.requestType).value(request.getRequestType())
                .isNullWithFailureCode(BillValidatorEnum.INVALID_REQUEST_TYPE).validateFieldMaxLengthWithFailureCodeAndErrorParams(
                        expectedRequestTypeLength, BillValidatorEnum.INVALID_REQUEST_TYPE_VALUE);

        if (request.getRequestType() != null
                && !(Objects.equals(request.getRequestType(), "00") || Objects.equals(request.getRequestType(), "01"))) {
            validatorBuilder.reset().resource(resource).parameter(BillPayDTOConstant.requestType).value(request.getRequestType())
                    .failWithCode(BillValidatorEnum.INVALID_REQUEST_TYPE_VALUE);
        }

        // Checks for payerFSPDetails
        if (request.getRequestType() != null && Objects.equals(request.getRequestType(), "00")) {
            validatorBuilder.reset().resource(resource).parameter(BillPayDTOConstant.payerFSPDetails).value(request.getPayerFspDetails())
                    .isNullWithFailureCode(BillValidatorEnum.INVALID_PAYER_FSP_DETAILS);

            if (request.getPayerFspDetails() == null) {
                request.setPayerFspDetails(new PayerFSPDetail());
            }
            validatePayerFSPDetails(request.getPayerFspDetails(), validatorBuilder);
        }

        // Checks for alias
        if (request.getRequestType() != null && Objects.equals(request.getRequestType(), "01")) {
            validatorBuilder.reset().resource(resource).parameter(BillPayDTOConstant.alias).value(request.getAlias())
                    .isNullWithFailureCode(BillValidatorEnum.INVALID_ALIAS);

            if (request.getAlias() == null) {
                request.setAlias(new Alias());
            }
            validateAlias(request.getAlias(), validatorBuilder);
        }

        // Checks for billDetails
        validatorBuilder.reset().resource(resource).parameter(BillPayDTOConstant.billDetails).value(request.getBillDetails())
                .isNullWithFailureCode(BillValidatorEnum.INVALID_BILL_DETAILS);
        if (request.getBillDetails() == null) {
            request.setBillDetails(new Bill());
        }
        validateBillDetails(request.getBillDetails(), validatorBuilder);

        return handleValidationErrors(validatorBuilder);
    }

    private void validatePayerFSPDetails(PayerFSPDetail payerFSPDetail, ValidatorBuilder validatorBuilder) {
        // Checks for unsupportedParameter
        unsupportedParameterValidator.handleUnsupportedParameterValidation(payerFSPDetail.getAdditionalProperties(), validatorBuilder);

        validatorBuilder.reset().resource(resource).parameter(BillPayDTOConstant.payerFSPID).value(payerFSPDetail.getPayerFSPID())
                .isNullWithFailureCode(BillValidatorEnum.INVALID_PAYER_FSP_ID).validateFieldMaxLengthWithFailureCodeAndErrorParams(
                        expectedPayerFSPIDLength, BillValidatorEnum.INVALID_PAYER_FSP_ID_LENGTH);

        validatorBuilder.reset().resource(resource).parameter(BillPayDTOConstant.financialAddress)
                .value(payerFSPDetail.getFinancialAddress()).isNullWithFailureCode(BillValidatorEnum.INVALID_FINANCIAL_ADDRESS)
                .validateFieldMaxLengthWithFailureCodeAndErrorParams(expectedFinancialAddressLength,
                        BillValidatorEnum.INVALID_FINANCIAL_ADDRESS_LENGTH);
    }

    private void validateAlias(Alias alias, ValidatorBuilder validatorBuilder) {
        // Checks for unsupportedParameter
        unsupportedParameterValidator.handleUnsupportedParameterValidation(alias.getAdditionalProperties(), validatorBuilder);

        validatorBuilder.reset().resource(resource).parameter(BillPayDTOConstant.aliasType).value(alias.getAliasType())
                .isNullWithFailureCode(BillValidatorEnum.INVALID_ALIAS_TYPE)
                .validateFieldMaxLengthWithFailureCodeAndErrorParams(expectedAliasTypeLength, BillValidatorEnum.INVALID_ALIAS_TYPE_VALUE);

        validatorBuilder.reset().resource(resource).parameter(BillPayDTOConstant.aliasId).value(alias.getAliasId()).ignoreIfNull()
                .validateFieldMaxLengthWithFailureCodeAndErrorParams(expectedAliasIdLength, BillValidatorEnum.INVALID_ALIAS_ID_LENGTH);
    }

    private void validateBillDetails(Bill bill, ValidatorBuilder validatorBuilder) {
        // Checks for unsupportedParameter
        unsupportedParameterValidator.handleUnsupportedParameterValidation(bill.getAdditionalProperties(), validatorBuilder);

        validatorBuilder.reset().resource(resource).parameter(BillPayDTOConstant.billerName).value(bill.getBillerName())
                .isNullWithFailureCode(BillValidatorEnum.INVALID_BILLER_NAME).validateFieldMaxLengthWithFailureCodeAndErrorParams(
                        expectedBillerNameLength, BillValidatorEnum.INVALID_BILLER_NAME_LENGTH);

        validatorBuilder.reset().resource(resource).parameter(BillPayDTOConstant.amount).value(bill.getAmount())
                .isNullWithFailureCode(BillValidatorEnum.INVALID_AMOUNT)
                .validateBigDecimalFieldNotNegativeWithFailureCode(BillValidatorEnum.INVALID_NEGATIVE_AMOUNT);
    }

    private PhErrorDTO handleValidationErrors(ValidatorBuilder validatorBuilder) {
        if (validatorBuilder.hasError()) {
            validatorBuilder.errorCategory(PaymentHubErrorCategory.Validation.toString())
                    .errorCode(BillValidatorEnum.BILL_SCHEMA_VALIDATION_ERROR.getCode())
                    .errorDescription(BillValidatorEnum.BILL_SCHEMA_VALIDATION_ERROR.getMessage())
                    .developerMessage(BillValidatorEnum.BILL_SCHEMA_VALIDATION_ERROR.getMessage())
                    .defaultUserMessage(BillValidatorEnum.BILL_SCHEMA_VALIDATION_ERROR.getMessage());

            PhErrorDTO.PhErrorDTOBuilder phErrorDTOBuilder = new PhErrorDTO.PhErrorDTOBuilder(ExtValidationError.getErrorCode());
            phErrorDTOBuilder.fromValidatorBuilder(validatorBuilder);
            return phErrorDTOBuilder.build();
        }
        return null;
    }
}
