package py.com.jmbr.mcs.icejas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.com.jmbr.java.commons.beans.mcs.icejas.TransactionPostReqData;
import py.com.jmbr.java.commons.beans.mcs.icejas.TransactionPostResData;
import py.com.jmbr.java.commons.context.SecurityAccess;
import py.com.jmbr.mcs.icejas.service.TransactionService;

@RestController
@RequestMapping(value = "transactions/${version}")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;
    @PostMapping(value = "/")
    @SecurityAccess()
    public TransactionPostResData addTransactions(@RequestBody TransactionPostReqData req){
        return transactionService.addTransactions(req.getData());
    }

}
