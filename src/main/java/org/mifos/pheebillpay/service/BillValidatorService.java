package org.mifos.pheebillpay.service;

import org.springframework.stereotype.Service;

@Service
public class BillValidatorService {

    // private static final String resource = "billValidator";
    // private static final String billId = "billId";
    // private static final int expectedBillIdLength = 10;
    // private static final String requestType = "requestType";
    // private static final String payerFspId = "payerFspId";
    // private static final String financialAddress = "financialAddress";
    // private static final String aliasType = "aliasType";
    // private static final String aliasId = "aliasId";
    //
    // public PhErrorDTO validateCreateVoucher(BillRTPReqDTO request) {
    // final ValidatorBuilder validatorBuilder = new ValidatorBuilder();
    //
    // validatorBuilder.reset().resource(resource).parameter(billId).value(request.getBillId())
    // .isNullWithFailureCode(BillValidatorEnum.INVALID_BILL_ID)
    // .validateFieldMaxLengthWithFailureCodeAndErrorParams(expectedBillIdLength,
    // BillValidatorEnum.INVALID_BILL_ID_LENGTH);
    // validatorBuilder.reset().resource(resource).parameter(requestType).value(request.getRequestType())
    // .isNullWithFailureCode(BillValidatorEnum.INVALID_REQUEST_TYPE);
    // validatorBuilder.reset().resource(resource).parameter(requestType).value(request.getRequestType())
    // .isNullWithFailureCode(BillValidatorEnum.INVALID_REQUEST_TYPE);
    // if (request.getRequestType().equals("00")) {
    // if (request.getPayerFspDetail() != null) {
    // validatorBuilder.reset().resource(resource).parameter(payerFspId).value(request.getPayerFspDetail().getPayerFspId())
    // .isNullWithFailureCode(BillValidatorEnum.INVALID_PAYER_FSP_ID);
    // validatorBuilder.reset().resource(resource).parameter(financialAddress)
    // .value(request.getPayerFspDetail().getFinancialAddress())
    // .isNullWithFailureCode(BillValidatorEnum.INVALID_FINANCIAL_ADDRESS);
    // } else {
    // validatorBuilder.reset().resource(resource).isNullWithFailureCode(BillValidatorEnum.INVALID_PAYER_FSP_DETAILS);
    // }
    // } else if (request.getRequestType().equals("01")) {
    // if (request.getAlias() != null) {
    // validatorBuilder.reset().resource(resource).parameter(aliasType).value(request.getAlias().getAliasType())
    // .isNullWithFailureCode(BillValidatorEnum.INVALID_ALIAS_TYPE);
    // validatorBuilder.reset().resource(resource).parameter(aliasId).value(request.getAlias().getAliasId())
    // .isNullWithFailureCode(BillValidatorEnum.INVALID_ALIAS_ID);
    //
    // if (request.getAlias().getAliasType().equals("00") || request.getAlias().getAliasType().equals("01")
    // || request.getAlias().getAliasType().equals("02")) {
    // validatorBuilder.reset().resource(resource).parameter(aliasType).value(request.getAlias().getAliasType())
    // .isNullWithFailureCode(BillValidatorEnum.INVALID_ALIAS_TYPE);
    // validatorBuilder.reset().resource(resource).parameter(aliasId).value(request.getAlias().getAliasId())
    // .isNullWithFailureCode(BillValidatorEnum.INVALID_ALIAS_ID);
    // } else {
    // validatorBuilder.reset().resource(resource).isNullWithFailureCode(BillValidatorEnum.INVALID_ALIAS_TYPE_VALUE);
    // }
    // } else {
    // validatorBuilder.reset().resource(resource).isNullWithFailureCode(BillValidatorEnum.INVALID_ALIAS_DETAILS);
    // }
    //
    // } else {
    // validatorBuilder.reset().resource(resource).isNullWithFailureCode(BillValidatorEnum.INVALID_REQUEST_TYPE_VALUE);
    // }
    //
    // // If errors exist, build and return PhErrorDTO
    // if (validatorBuilder.hasError()) {
    // validatorBuilder.errorCategory(PaymentHubErrorCategory.Validation.toString())
    // .errorCode(BillValidatorEnum.BILL_SCHEMA_VALIDATION_ERROR.getCode())
    // .errorDescription(BillValidatorEnum.BILL_SCHEMA_VALIDATION_ERROR.getMessage())
    // .developerMessage(BillValidatorEnum.BILL_SCHEMA_VALIDATION_ERROR.getMessage())
    // .defaultUserMessage(BillValidatorEnum.BILL_SCHEMA_VALIDATION_ERROR.getMessage());
    //
    // PhErrorDTO.PhErrorDTOBuilder phErrorDTOBuilder = new
    // PhErrorDTO.PhErrorDTOBuilder(ExtValidationError.getErrorCode());
    // phErrorDTOBuilder.fromValidatorBuilder(validatorBuilder);
    // return phErrorDTOBuilder.build();
    // }
    //
    // return null;
    // }
}
