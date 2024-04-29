package py.com.jmbr.mcs.icejas.service;

import com.lowagie.text.DocumentException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import py.com.jmbr.java.commons.beans.mcs.icejas.*;
import py.com.jmbr.java.commons.domain.mcs.icejas.*;
import py.com.jmbr.java.commons.exception.JMBRException;
import py.com.jmbr.java.commons.exception.JMBRExceptionType;
import py.com.jmbr.java.commons.logger.RequestUtil;
import py.com.jmbr.mcs.icejas.constant.TransactionConstant;
import py.com.jmbr.mcs.icejas.dao.TransactionDAO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService{
    public static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
    @Autowired
    private TransactionDAO transactionDAO;
    @Autowired
    private TemplateEngine templateEngine;
    @Override
    public TransactionPostResData addTransactions(TransactionPostReq req) {
        TransactionPostResData result = new TransactionPostResData();
        TransactionPostRes resultData = new TransactionPostRes();
        Transaction transaction = req.getTransaction();
        String logId = RequestUtil.getLogId();
        
        logger.debug(RequestUtil.LOG_FORMATT,logId,"addTransactions:Starting add transaction",req);

        logger.debug(RequestUtil.LOG_FORMATT,logId,"addTransactions:Before add transaction ",transaction);
        Integer transactionId = transactionDAO.addTransaction(transaction,req.getChurch().getId(),req.getTransactionType().getId(), req.getUserId());
        logger.debug(RequestUtil.LOG_FORMATT,logId,"addTransactions:After add transaction id= ",transactionId);
         
        BigDecimal currentAmount = transactionDAO.getChurchCurrentBalance(logId,req.getChurch().getId());
        logger.info(RequestUtil.LOG_FORMATT,logId,"addTransactions:Current balances",currentAmount);

        BigDecimal totalAmount = currentAmount;
        if(req.getTransactionType().getCategory().equals(TransactionConstant.TRANSACTION_DEBIT))
            totalAmount = totalAmount.subtract(transaction.getAmount());
        else
            totalAmount = totalAmount.add(transaction.getAmount());

        logger.debug(RequestUtil.LOG_FORMATT,logId,"addTransactions:Before update total amount to church ",false);
        boolean isTotalAmountUpdated = transactionDAO.updateBalanceChurch(logId,req.getChurch().getId(),totalAmount);
        logger.debug(RequestUtil.LOG_FORMATT,logId,"addTransactions:After update total amount to church ",isTotalAmountUpdated);
        resultData.setTransactionId(transactionId);
        resultData.setMessage(TransactionConstant.MESSAGE_SUCCESS);
        resultData.setStatus(TransactionConstant.STATUS_OK);
        result.setData(resultData);
        return result;
    }

    @Override
    public Church getChurch(Integer churchId) {
        String logId = RequestUtil.getLogId();
        logger.debug(RequestUtil.LOG_FORMATT,logId,"getChurch:Starting get church",churchId);
        Church church =  transactionDAO.getChurch(logId,churchId);
        logger.debug(RequestUtil.LOG_FORMATT,logId,"getChurch:After get church",church);
        return church;
    }

    @Override
    public TransactionTypesGetResData getTransactionTypes() {
        String logId = RequestUtil.getLogId();
        TransactionTypesGetResData result = new TransactionTypesGetResData();
        TransactionTypesGetRes data = new TransactionTypesGetRes();
        logger.debug(RequestUtil.LOG_FORMATT,logId,"getTransactionTypes:Starting get transcationTypes",null);
        logger.debug(RequestUtil.LOG_FORMATT,logId,"getTransactionTypes:Before get transcationTypes",null);
        List<TransactionType> transactionTypes = transactionDAO.getTransactionTypes(logId);
        logger.debug(RequestUtil.LOG_FORMATT,logId,"getTransactionTypes:After get transcationTypes",transactionTypes.size());

        data.setTransactionTypes(transactionTypes);
        result.setData(data);
        return result;
    }

    @Override
    public TransactionDetailGetResData getTransactionDetails(Integer churchId,String startDateStr,String endDateStr,Integer activiteType,String transactionType) {
        String logId = RequestUtil.getLogId();
        TransactionDetailGetResData result = new TransactionDetailGetResData();
        TransactionDetailGetRes data = new TransactionDetailGetRes();
        logger.debug(RequestUtil.LOG_FORMATT,logId,"getTransactionDetails:Starting GET transaction details",null);
        logger.debug(RequestUtil.LOG_FORMATT,logId,"getTransactionDetails:Before get all transaction details churchId=",churchId);

        List<TransactionDetails> details = transactionDAO.getTransactionDetails(logId,churchId,startDateStr,endDateStr,activiteType,transactionType);
        logger.debug(RequestUtil.LOG_FORMATT,logId,"getTransactionDetails:After get all transaction details churchId=",details.size());
        data.setDetails(details);
        result.setData(data);

        return result;
    }

    @Override
    public TransactionTypesPostRestData addTransactionType(TransactionType transactionType) {
        String logId = RequestUtil.getLogId();
        TransactionTypesPostRestData result = new TransactionTypesPostRestData();
        TransactionTypesPostRes data  = new TransactionTypesPostRes();
        logger.debug(RequestUtil.LOG_FORMATT,logId,"addTransactionType:Starting POST transaction type",null);
        logger.debug(RequestUtil.LOG_FORMATT,logId,"addTransactionType:Before add transaction type",transactionType.toString());
        Boolean isInserted = transactionDAO.addTransactionType(logId,transactionType);
        logger.debug(RequestUtil.LOG_FORMATT,logId,"addTransactionType:After add  transaction type result=",isInserted);

        data.setIsInserted(isInserted);
        result.setData(data);
        return result;
    }

    @Override
    public TransactionMonthClosedPostResData closedMonth(Integer userId, Date closedDate) {
        return null;
    }

    @Override
    public TransactionReportGetResData getReportMonth(Integer churchId) {
        String logId = RequestUtil.getLogId();
        logger.debug(RequestUtil.LOG_FORMATT,logId,"getReportMonth:Starting GET transaction report",null);
        TransactionReportGetResData result = new TransactionReportGetResData();
        logger.debug(RequestUtil.LOG_FORMATT,logId,"getReportMonth:Before get transaction report",null);
        List<TransactionReportGetRes> transactionsReport = transactionDAO.getReportMonth(churchId,logId);
        logger.debug(RequestUtil.LOG_FORMATT,logId,"getReportMonth:After get transaction report with result=",transactionsReport.size());
        if(transactionsReport.isEmpty())
            throw new JMBRException("No se obtuvo ningun movimiento", JMBRExceptionType.WARNING, HttpStatus.BAD_REQUEST);

        result.setData(transactionsReport);
        return result;
    }

    @Override
    public TransactionPutResData updateTransaction(TransactionPostReq req) {
        TransactionPutResData result = new TransactionPutResData();
        TransactionPutRes data = new TransactionPutRes();
        String logId = RequestUtil.getLogId();
        logger.debug(RequestUtil.LOG_FORMATT,logId,"updateTransaction:Starting UDPATE transaction",req.toString());
        logger.debug(RequestUtil.LOG_FORMATT,logId,"updateTransaction:Before update transaction ",false);
        boolean isTransactionUpdated = transactionDAO.updateTransaction(logId,req.getTransaction(),req.getTransactionType().getId());
        logger.debug(RequestUtil.LOG_FORMATT,logId,"updateTransaction:Before update transaction ",isTransactionUpdated);

        BigDecimal newTotalAmount = transactionDAO.getTotalAmount(logId,req.getChurch().getId());
        logger.debug(RequestUtil.LOG_FORMATT,logId,"updateTransaction:Before update total amount to church ",false);
        boolean isTotalAmountUpdated = transactionDAO.updateBalanceChurch(logId,req.getChurch().getId(),newTotalAmount);
        logger.debug(RequestUtil.LOG_FORMATT,logId,"updateTransaction:Before update total amount to church ",isTotalAmountUpdated);

        data.setMessage("Actualizacion exitosa!");
        data.setStatus(Boolean.TRUE);
        result.setData(data);

        return result;
    }

    @Override
    public TransactionDeleteResData deleteTransaction(Integer transactionId,Integer churchId) {
        String logId = RequestUtil.getLogId();
        TransactionDeleteResData result = new TransactionDeleteResData();
        TransactionDeleteRes resData = new TransactionDeleteRes();
        logger.debug(RequestUtil.LOG_FORMATT,logId,"deleteTransaction:Starting DELETE transaction id=",transactionId);
        boolean isUpdatedTransaction = transactionDAO.deleteTransaction(logId,transactionId);
        logger.debug(RequestUtil.LOG_FORMATT,logId,"deleteTransaction:After deleting transaction ",isUpdatedTransaction);

        BigDecimal newTotalAmount = transactionDAO.getTotalAmount(logId,churchId);
        logger.debug(RequestUtil.LOG_FORMATT,logId,"deleteTransaction:Before update total amount to church ",false);
        boolean isTotalAmountUpdated = transactionDAO.updateBalanceChurch(logId,churchId,newTotalAmount);
        logger.debug(RequestUtil.LOG_FORMATT,logId,"deleteTransaction:Before update total amount to church ",isTotalAmountUpdated);

        resData.setStatus(Boolean.TRUE);
        resData.setStatusDescription("Movimiento eliminado exitosamente!");
        result.setData(resData);
        return result;
    }

    @Override
    public MonthSummaryGetResData getMonthSummary(String startMonth, String endMonth, Integer churchId) {
        String logId = RequestUtil.getLogId();
        MonthSummaryGetResData result  = new MonthSummaryGetResData();
        logger.debug(RequestUtil.LOG_FORMATT,logId,"getMonthSummary:Starting GET summaryMonths ",startMonth+":"+endMonth);
        List<MonthSummaryGetRes> summaryMonths = transactionDAO.getSummaryMonths(startMonth,endMonth,churchId,logId);
        logger.debug(RequestUtil.LOG_FORMATT,logId,"getMonthSummary:After getting summaryMonths ",summaryMonths.size());
        BigDecimal totalSum = summaryMonths
                .stream()
                .map(MonthSummaryGetRes::getTotalSum) // Extraer el valor total de cada objeto MonthSummaryGetRes
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        logger.debug(RequestUtil.LOG_FORMATT,logId,"getMonthSummary:After getting totalSum =  ",totalSum);
        result.setMonths(summaryMonths);
        result.setTotalSum(totalSum);
        return result;
    }

    @Override
    public byte[] getReportPDF(String startMonth, String endMonth, Integer churchId) throws IOException, DocumentException {
        String logId = RequestUtil.getLogId();
        logger.debug(RequestUtil.LOG_FORMATT,logId,"getReportPDF:Starting GET summaryMonths ",startMonth+":"+endMonth);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Context context = new Context();

        //get
        MonthSummaryGetResData monthSummaryGetResData = getMonthSummary(startMonth,endMonth,churchId);

        context.setVariable("church","ICEJAS");
        context.setVariable("reports",monthSummaryGetResData.getMonths());
        context.setVariable("totalAmount",monthSummaryGetResData.getTotalSum());

        String html = templateEngine.process("reports",context);
        ITextRenderer renderer  = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);
        byte[] pdfBytes = outputStream.toByteArray();

        outputStream.close();

        return pdfBytes;
    }

}
