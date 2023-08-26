package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ObjectMapperSingleton;
import org.example.dto.ExchangeDto;
import org.example.model.exception.ApiError;
import org.example.service.ExchangeRateService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@WebServlet("/exchange/*")
public class ExchangeServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        objectMapper = ObjectMapperSingleton.getInstance();
        exchangeRateService = ExchangeRateService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String fromCurrencyCode = req.getParameter("from");
        String toCurrencyCode = req.getParameter("to");
        String amountStr = req.getParameter("amount");

        if (toCurrencyCode != null && fromCurrencyCode != null && amountStr != null && Double.parseDouble(amountStr) > 0) {
            ExchangeDto foundExchange = exchangeRateService.convertCurrency(fromCurrencyCode, toCurrencyCode, Double.parseDouble(amountStr));
            String resultJson = objectMapper.writeValueAsString(foundExchange);
            resp.getWriter().write(resultJson);
        } else {
            setErrorMassageResp(resp, "Error in request parameters", HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    private void setErrorMassageResp(HttpServletResponse resp, String message, int responseStatus) throws IOException {
        String resultJson = objectMapper.writeValueAsString(new ApiError(message, new Date()));
        resp.setStatus(responseStatus);
        resp.getWriter().write(resultJson);
    }
}
