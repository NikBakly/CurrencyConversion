package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ObjectMapperSingleton;
import org.example.dto.ExchangeRateDto;
import org.example.model.ExchangeRate;
import org.example.model.exception.ApiError;
import org.example.model.exception.ConflictException;
import org.example.model.exception.InternalServerErrorException;
import org.example.model.exception.NotFoundException;
import org.example.service.ExchangeRateService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@WebServlet("/exchangeRates/*")
public class ExchangeRateServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        exchangeRateService = ExchangeRateService.getInstance();
        objectMapper = ObjectMapperSingleton.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String resultJson;
        try {
            if (pathInfo == null) {
                List<ExchangeRate> foundExchangeRates = exchangeRateService.getAll();
                resultJson = objectMapper.writeValueAsString(foundExchangeRates);
                resp.getWriter().write(resultJson);
            } else {
                String[] pathPars = pathInfo.split("/");
                if (pathPars.length > 1) {
                    String codes = pathPars[1].trim().toUpperCase();
                    ExchangeRate foundExchangeRate = exchangeRateService.getByCodes(codes);
                    if (foundExchangeRate != null) {
                        resultJson = objectMapper.writeValueAsString(foundExchangeRate);
                        resp.getWriter().write(resultJson);
                    }
                } else {
                    setErrorMassageResp(
                            resp, "Error in the request path parameter", HttpServletResponse.SC_BAD_REQUEST);
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            }
        } catch (NotFoundException e) {
            setErrorMassageResp(resp, e.getMessage(), HttpServletResponse.SC_NOT_FOUND);
        } catch (InternalServerErrorException e) {
            setErrorMassageResp(resp, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            ExchangeRateDto exchangeRateDto = objectMapper.readValue(req.getReader(), ExchangeRateDto.class);
            exchangeRateService.create(exchangeRateDto);
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } catch (InternalServerErrorException e) {
            setErrorMassageResp(resp, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (ConflictException e) {
            setErrorMassageResp(resp, e.getMessage(), HttpServletResponse.SC_CONFLICT);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ExchangeRateDto exchangeRateDto = objectMapper.readValue(req.getReader(), ExchangeRateDto.class);
            Integer exchangeRateId = getExchangeIdParameter(req);
            if (exchangeRateId == null) {
                setErrorMassageResp(resp,
                        "id not found in the request path parameter",
                        HttpServletResponse.SC_BAD_REQUEST);
            } else {
                exchangeRateService.updateById(exchangeRateId, exchangeRateDto);
            }
        } catch (InternalServerErrorException e) {
            setErrorMassageResp(resp, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (ConflictException e) {
            setErrorMassageResp(resp, e.getMessage(), HttpServletResponse.SC_CONFLICT);
        } catch (NotFoundException e) {
            setErrorMassageResp(resp, e.getMessage(), HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Integer exchangeRateId = getExchangeIdParameter(req);
            if (exchangeRateId == null) {
                setErrorMassageResp(resp,
                        "id not found in the request path parameter",
                        HttpServletResponse.SC_BAD_REQUEST);
            } else {
                exchangeRateService.deleteById(exchangeRateId);
            }
        } catch (NotFoundException e) {
            setErrorMassageResp(resp, e.getMessage(), HttpServletResponse.SC_NOT_FOUND);
        } catch (InternalServerErrorException e) {
            setErrorMassageResp(resp, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


    private void setErrorMassageResp(HttpServletResponse resp, String message, int responseStatus) throws IOException {
        String resultJson = objectMapper.writeValueAsString(new ApiError(message, new Date()));
        resp.setStatus(responseStatus);
        resp.getWriter().write(resultJson);
    }

    private Integer getExchangeIdParameter(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null) {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length > 1) {
                return Integer.parseInt(pathParts[1]);
            }
        }
        return null;
    }
}
