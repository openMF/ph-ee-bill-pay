package org.mifos.pheebillpay.utils;

import org.mifos.connector.common.exception.PaymentHubErrorCategory;
import org.mifos.connector.common.validation.ValidationCodeType;

public enum BillValidatorEnum implements ValidationCodeType {

    HEADER_VALIDATION_ERROR("error.msg.header.validation.errors", "The headers are invalid"), BILL_SCHEMA_VALIDATION_ERROR(
            "error.msg.schema.validation.errors",
            "The request is invalid"), INVALID_CORRELATION_ID("error.msg.schema.correlation.id.cannot.be.null.or.empty",
                    "Correlation ID cannot be null or empty"), INVALID_CORRELATION_ID_LENGTH(
                            "error.msg.schema.correlation.id.length.is.invalid",
                            "Correlation ID length is invalid"), INVALID_TENANT_ID("error.msg.schema.tenant.id.cannot.be.null.or.empty",
                                    "Tenant ID cannot be null or empty"), INVALID_TENANT_ID_LENGTH(
                                            "error.msg.schema.tenant.id.length.is.invalid",
                                            "Tenant ID length is invalid"), INVALID_PAYER_FSP_ID(
                                                    "error.msg.schema.payer.fsp.id.cannot.be.null.or.empty",
                                                    "Payer FSP ID cannot be null or empty"), INVALID_PAYER_FSP_ID_LENGTH(
                                                            "error.msg.schema.payer.fsp.id.length.is.invalid",
                                                            "Payer FSP ID length is invalid"), INVALID_CLIENT_CORRELATION_ID(
                                                                    "error.msg.schema.client.correlation.id.cannot.be.null.or.empty",
                                                                    "Client Correlation ID cannot be null or empty"), INVALID_CLIENT_CORRELATION_ID_LENGTH(
                                                                            "error.msg.schema.client.correlation.id.length.is.invalid",
                                                                            "Client Correlation ID length is invalid"), INVALID_PLATFORM_TENANT_ID(
                                                                                    "error.msg.schema.platform.tenant.id.cannot.be.null.or.empty",
                                                                                    "Platform Tenant ID cannot be null or empty"), INVALID_PLATFORM_TENANT_ID_LENGTH(
                                                                                            "error.msg.schema.platform.tenant.id.length.is.invalid",
                                                                                            "Platform Tenant ID length is invalid"), INVALID_BILLER_ID(
                                                                                                    "error.msg.schema.biller.id.cannot.be.null.or.empty",
                                                                                                    "Biller ID cannot be null or empty"), INVALID_BILLER_ID_LENGTH(
                                                                                                            "error.msg.schema.biller.id.length.is.invalid",
                                                                                                            "Biller ID length is invalid"), INVALID_CALLBACK_URL(
                                                                                                                    "error.msg.schema.callback.url.cannot.be.null.or.empty",
                                                                                                                    "Callback URL cannot be null or empty"), INVALID_CALLBACK_URL_LENGTH(
                                                                                                                            "error.msg.schema.callback.url.length.is.invalid",
                                                                                                                            "Callback URL length is invalid"), INVALID_REGISTERING_INSTITUTION_ID(
                                                                                                                                    "error.msg.schema.registering.institution.id.cannot.be.null.or.empty",
                                                                                                                                    "Registering Institution ID cannot be null or empty"), INVALID_REGISTERING_INSTITUTION_ID_LENGTH(
                                                                                                                                            "error.msg.schema.registering.institution.id.length.is.invalid",
                                                                                                                                            "Registering Institution ID length is invalid"), INVALID_BILL_ID(
                                                                                                                                                    "error.msg.schema.bill.id.cannot.be.null.or.empty",
                                                                                                                                                    "Bill ID cannot be null or empty"), INVALID_BILL_ID_LENGTH(
                                                                                                                                                            "error.msg.schema.bill.id.length.is.invalid",
                                                                                                                                                            "Bill ID length is invalid"), INVALID_REQUEST_TYPE(
                                                                                                                                                                    "error.msg.schema.request.type.cannot.be.null.or.empty",
                                                                                                                                                                    "Request Type cannot be null or empty"), INVALID_REQUEST_TYPE_VALUE(
                                                                                                                                                                            "error.msg.schema.request.type.is.invalid",
                                                                                                                                                                            "Request Type is Invalid"), INVALID_FINANCIAL_ADDRESS(
                                                                                                                                                                                    "error.msg.schema.financial.address.cannot.be.null.or.empty",
                                                                                                                                                                                    "Financial Address cannot be null or empty"), INVALID_FINANCIAL_ADDRESS_LENGTH(
                                                                                                                                                                                            "error.msg.schema.financial.address.length.is.invalid",
                                                                                                                                                                                            "Financial Address length is invalid"), INVALID_PAYER_FSP_DETAILS(
                                                                                                                                                                                                    "error.msg.schema.payer.fsp.details.cannot.be.null.or.empty",
                                                                                                                                                                                                    "Payer Fsp details cannot be null or empty"), INVALID_ALIAS(
                                                                                                                                                                                                            "error.msg.schema.alias.cannot.be.null.or.empty",
                                                                                                                                                                                                            "alias cannot be null or empty"), INVALID_ALIAS_ID(
                                                                                                                                                                                                                    "error.msg.schema.alias.id.cannot.be.null.or.empty",
                                                                                                                                                                                                                    "Alias Id cannot be null or empty"), INVALID_ALIAS_ID_LENGTH(
                                                                                                                                                                                                                            "error.msg.schema.alias.id.length.is.invalid",
                                                                                                                                                                                                                            "Alias Id length is invalid"), INVALID_ALIAS_TYPE(
                                                                                                                                                                                                                                    "error.msg.schema.alias.type.cannot.be.null.or.empty",
                                                                                                                                                                                                                                    "Alias Type cannot be null or empty"), INVALID_ALIAS_TYPE_VALUE(
                                                                                                                                                                                                                                            "error.msg.schema.alias.type.is.invalid",
                                                                                                                                                                                                                                            "Alias Type is Invalid"), INVALID_ALIAS_DETAILS(
                                                                                                                                                                                                                                                    "error.msg.schema.alias.cannot.be.null.or.empty",
                                                                                                                                                                                                                                                    "Alias cannot be null or empty"), INVALID_BILL_DETAILS(
                                                                                                                                                                                                                                                            "error.msg.schema.bill.details.cannot.be.null.or.empty",
                                                                                                                                                                                                                                                            "bill details cannot be null or empty"), INVALID_BILLER_NAME(
                                                                                                                                                                                                                                                                    "error.msg.schema.biller.name.cannot.be.null.or.empty",
                                                                                                                                                                                                                                                                    "Biller Name cannot be null or empty"), INVALID_BILLER_NAME_LENGTH(
                                                                                                                                                                                                                                                                            "error.msg.schema.biller.name.length.is.invalid",
                                                                                                                                                                                                                                                                            "Biller Name length is invalid"), INVALID_AMOUNT(
                                                                                                                                                                                                                                                                                    "error.msg.schema.amount.cannot.be.null.or.empty",
                                                                                                                                                                                                                                                                                    "Amount cannot be null or empty"), INVALID_NEGATIVE_AMOUNT(
                                                                                                                                                                                                                                                                                            "error.msg.schema.amount.cannot.be.negative",
                                                                                                                                                                                                                                                                                            "Amount cannot be negative"),

    INVALID_BILL_INQUIRY_REQUEST_ID("error.msg.schema.bill.inquiry.request.id.cannot.be.null.or.empty",
            "Bill Inquiry Request ID cannot be null or empty"), INVALID_BILL_INQUIRY_REQUEST_ID_LENGTH(
                    "error.msg.schema.bill.inquiry.request.id.length.is.invalid",
                    "Bill Inquiry Request ID length is invalid"), INVALID_PAYMENT_REFERENCE_ID(
                            "error.msg.schema.payment.reference.id.cannot.be.null.or.empty",
                            "Payment Reference ID cannot be null or empty"), INVALID_PAYMENT_REFERENCE_ID_LENGTH(
                                    "error.msg.schema.payment.reference.id.length.is.invalid", "Payment Reference ID length is invalid");

    private final String code;
    private final String category;
    private final String message;

    BillValidatorEnum(String code, String message) {
        this.code = code;
        this.category = PaymentHubErrorCategory.Validation.toString();
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getCategory() {
        return this.category;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
