package org.mifos.pheeBillPay.utils;

import org.mifos.connector.common.exception.PaymentHubErrorCategory;
import org.mifos.connector.common.validation.ValidationCodeType;

public enum BillValidatorEnum implements ValidationCodeType {
    BILL_SCHEMA_VALIDATION_ERROR(
            "error.msg.schema.validation.errors",
            "The request is invalid"),
    INVALID_BILL_ID(
            "error.msg.schema.bill.id.cannot.be.null.or.empty",
            "Bill ID cannot be null or empty"), INVALID_BILL_ID_LENGTH(
            "error.msg.schema.bill.id.length.is.invalid", "Bill ID length is invalid"),
    INVALID_REQUEST_TYPE(
            "error.msg.schema.request.type.cannot.be.null.or.empty",
            "Request Type cannot be null or empty"), INVALID_REQUEST_TYPE_VALUE(
            "error.msg.schema.request.type.is.invalid",
            "Request Type is Invalid"),
    INVALID_PAYER_FSP_ID(
            "error.msg.schema.payer.fsp.id.cannot.be.null.or.empty",
            "Payer Fsp Id cannot be null or empty"), INVALID_FINANCIAL_ADDRESS(
            "error.msg.schema.financial.address.cannot.be.null.or.empty",
            "Financial Address cannot be null or empty"), INVALID_PAYER_FSP_DETAILS(
            "error.msg.schema.payer.fsp.details.cannot.be.null.or.empty",
                    "Payer Fsp details cannot be null or empty"),
    INVALID_ALIAS_ID(
            "error.msg.schema.alias.id.cannot.be.null.or.empty",
            "Alias Id cannot be null or empty"), INVALID_ALIAS_TYPE(
            "error.msg.schema.alias.type.cannot.be.null.or.empty",
            "Alias Type cannot be null or empty"),INVALID_ALIAS_TYPE_VALUE(
            "error.msg.schema.alias.type.is.invalid",
            "Alias Type is Invalid"), INVALID_ALIAS_DETAILS(
            "error.msg.schema.alias.cannot.be.null.or.empty",
            "Alias cannot be null or empty")
    ;
    private final String code;
    private final String category;
    private final String message;

    BillValidatorEnum(String code, String message) {
        this.code = code;
        this.category = PaymentHubErrorCategory.Validation.toString();
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getCategory() {
        return this.category;
    }

    public String getMessage() {
        return message;
    }
}
