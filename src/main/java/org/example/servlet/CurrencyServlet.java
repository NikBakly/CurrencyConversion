package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ObjectMapperSingleton;
import org.example.dao.CurrencyDao;
import org.example.dao.CurrencyDaoImpl;
import org.example.dto.CurrencyDto;
import org.example.model.ApiError;
import org.example.model.Currency;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

@WebServlet("/currencies/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyDao currencyDao;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        currencyDao = CurrencyDaoImpl.getInstance();
        objectMapper = ObjectMapperSingleton.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setReqAndResp(req, resp);
        String pathInfo = req.getPathInfo();
        String resultJson;
        try {
            if (pathInfo == null) {
                List<Currency> foundCurrencies = currencyDao.getAll();
                resultJson = objectMapper.writeValueAsString(foundCurrencies);
                resp.getWriter().write(resultJson);
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length > 1) {
                    String code = pathParts[1];
                    Currency foundCurrency = currencyDao.getCurrencyByCode(code);
                    if (foundCurrency != null) {
                        resultJson = objectMapper.writeValueAsString(foundCurrency);
                        resp.getWriter().write(resultJson);
                    }
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            }
        } catch (RuntimeException e) {
            setErrorMassageResp(resp, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setReqAndResp(req, resp);
        try {
            CurrencyDto currencyDto = objectMapper.readValue(req.getReader(), CurrencyDto.class);
            currencyDao.create(currencyDto);
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } catch (RuntimeException e) {
            setErrorMassageResp(resp, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setReqAndResp(req, resp);
        try {
            CurrencyDto currencyDto = objectMapper.readValue(req.getReader(), CurrencyDto.class);
            Integer currencyId = getCurrencyIdParameter(req);
            if (currencyId == null) {
                setErrorMassageResp(resp, "id not found", HttpServletResponse.SC_BAD_REQUEST);
            } else {
                currencyDao.updateById(currencyId, currencyDto);
            }
        } catch (RuntimeException e) {
            setErrorMassageResp(resp, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setReqAndResp(req, resp);
        try {
            Integer currencyId = getCurrencyIdParameter(req);
            if (currencyId == null) {
                setErrorMassageResp(resp, "id not found", HttpServletResponse.SC_BAD_REQUEST);
            } else {
                currencyDao.deleteById(currencyId);
            }
        } catch (RuntimeException e) {
            setErrorMassageResp(resp, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void setReqAndResp(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");
    }

    private Integer getCurrencyIdParameter(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null) {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length > 1) {
                return Integer.parseInt(pathParts[1]);
            }
        }
        return null;
    }

    private void setErrorMassageResp(HttpServletResponse resp, String message, int responseStatus) throws IOException {
        String resultJson = objectMapper.writeValueAsString(new ApiError(message, new Date()));
        resp.setStatus(responseStatus);
        resp.getWriter().write(resultJson);
    }
}