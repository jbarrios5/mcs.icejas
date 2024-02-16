package py.com.jmbr.mcs.icejas.service;

import py.com.jmbr.java.commons.beans.mcs.icejas.*;
import py.com.jmbr.java.commons.domain.mcs.icejas.Church;
import py.com.jmbr.java.commons.domain.mcs.icejas.TransactionPostReq;
import py.com.jmbr.java.commons.domain.mcs.icejas.TransactionType;

import java.sql.Date;

public interface TransactionService {
    TransactionPostResData addTransactions(TransactionPostReq req);

    TransactionPutResData updateTransaction(TransactionPostReq req);
    Church getChurch(Integer churchId);

    TransactionTypesGetResData getTransactionTypes();

    TransactionDetailGetResData getTransactionDetails(Integer churchId,String startDate,String endDate,Integer activiteType,String transactionType);

    TransactionTypesPostRestData addTransactionType(TransactionType transactionType);

    TransactionMonthClosedPostResData closedMonth(Integer userId, Date closedDate);

    TransactionReportGetResData getReportMonth(Integer churchId);
}
