package py.com.jmbr.mcs.icejas.service;

import py.com.jmbr.java.commons.beans.mcs.icejas.TransactionPostResData;
import py.com.jmbr.java.commons.beans.mcs.icejas.TransactionTypesGetResData;
import py.com.jmbr.java.commons.domain.mcs.icejas.Church;
import py.com.jmbr.java.commons.domain.mcs.icejas.TransactionPostReq;

public interface TransactionService {
    TransactionPostResData addTransactions(TransactionPostReq req);

    Church getChurch(Integer churchId);

    TransactionTypesGetResData getTransactionTypes();
}
