package org.mifos.pheebillpay.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.mifos.connector.common.channel.dto.PhErrorDTO;
import org.mifos.connector.common.validation.ValidatorBuilder;
import org.mifos.pheebillpay.api.definition.BillPaymentsApi;
import org.mifos.pheebillpay.api.definition.BillRtpReqApi;
import org.mifos.pheebillpay.api.implementation.BillInquiryController;
import org.mifos.pheebillpay.service.HeaderBuilderService;
import org.mifos.pheebillpay.validators.HeaderValidator;
import org.mifos.pheebillpay.validators.UnsupportedParameterValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class ValidatorInterceptor implements HandlerInterceptor {

    private static final String resource = "ValidatorInterceptor";
    private static final String callbackURL = "X-CallbackURL";
    private static final String registeringInstitutionId = "X-Registering-Institution-ID";

    @Autowired
    HeaderBuilderService headerBuilderService;
    @Autowired
    UnsupportedParameterValidator unsupportedParameterValidator;
    @Autowired
    HeaderValidator headerValidator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        log.debug("request at interceptor");
        PhErrorDTO phErrorDTO = null;

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            final ValidatorBuilder validatorBuilder = new ValidatorBuilder();

            if (handlerMethod.getBeanType().equals(BillInquiryController.class)) {
                phErrorDTO = headerValidator.validateBillInquiryRequest(request);
            } else if (handlerMethod.getBeanType().equals(BillPaymentsApi.class)) {
                phErrorDTO = headerValidator.validateBillPaymentRequest(request);
            } else if (handlerMethod.getBeanType().equals(BillRtpReqApi.class)) {
                phErrorDTO = headerValidator.validateBillRTPRequest(request);
            }
        }

        return checkForErrors(phErrorDTO, response);
    }

    private boolean checkForErrors(PhErrorDTO phErrorDTO, HttpServletResponse response) throws IOException {
        if (phErrorDTO != null) {

            // Converting PHErrorDTO in JSON Format
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(phErrorDTO);

            // Setting response status and writing the error message
            response.setHeader("Content-Type", "application/json");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write(jsonResponse);

            return false;
        }

        return true;
    }

}
