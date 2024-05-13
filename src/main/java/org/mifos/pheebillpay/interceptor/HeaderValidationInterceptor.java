package org.mifos.pheebillpay.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.mifos.pheebillpay.service.ValidateHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class HeaderValidationInterceptor implements HandlerInterceptor {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("At interceptor");
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            if (method.isAnnotationPresent(ValidateHeaders.class)) {
                ValidateHeaders validateHeaders = method.getAnnotation(ValidateHeaders.class);
                Set<String> headersSet = extractRequiredHeaders(validateHeaders);

                Object validatorInstance = getValidatorInstance(validateHeaders);

                Object methodResponse = invokeValidationMethod(validateHeaders, validatorInstance, headersSet, request);

                if (methodResponse != null) {
                    handleValidationFailure(response, methodResponse);
                    return false;
                }
            }
        }
        return true;
    }

    private Set<String> extractRequiredHeaders(ValidateHeaders validateHeaders) {
        return Arrays.stream(validateHeaders.requiredHeaders()).map(String::toLowerCase).collect(Collectors.toSet());
    }

    private Object getValidatorInstance(ValidateHeaders validateHeaders) {
        return applicationContext.getBean(validateHeaders.validatorClass());
    }

    private Object invokeValidationMethod(ValidateHeaders validateHeaders, Object validatorInstance, Set<String> headersSet,
            HttpServletRequest request) throws Exception {
        Method validationMethod = validatorInstance.getClass().getDeclaredMethod(validateHeaders.validationFunction(), Set.class,
                HttpServletRequest.class);
        Object[] parameters = { headersSet, request };
        return validationMethod.invoke(validatorInstance, parameters);
    }

    private void handleValidationFailure(HttpServletResponse response, Object methodResponse) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(methodResponse);
        response.setHeader("Content-Type", "application/json");
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.getWriter().write(jsonResponse);

        log.info("Interceptor response is : {}", jsonResponse);
    }

}
