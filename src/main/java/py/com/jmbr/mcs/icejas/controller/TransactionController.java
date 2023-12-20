package py.com.jmbr.mcs.icejas.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import py.com.jmbr.java.commons.beans.mcs.icejas.TransactionPostReqData;
import py.com.jmbr.java.commons.beans.mcs.icejas.TransactionPostResData;
import py.com.jmbr.java.commons.beans.mcs.icejas.TransactionTypesGetResData;
import py.com.jmbr.java.commons.context.SecurityAccess;
import py.com.jmbr.java.commons.domain.mcs.icejas.Church;
import py.com.jmbr.mcs.icejas.service.TransactionService;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "transactions/${version}")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;
    @PostMapping(value = "/")
    public TransactionPostResData addTransactions(@RequestBody @Valid TransactionPostReqData req){
        return transactionService.addTransactions(req.getData());
    }

    @GetMapping("/types")
    @Operation(summary = "get transactions types",description = "Get all transactions types ")
    public TransactionTypesGetResData getTransactionTypes(){
        return transactionService.getTransactionTypes();
    }
    @GetMapping("/church")
    @Operation(summary = "get church",description = "Get church details ")
    public Church getChurch(@RequestParam(value = "church_id",required = true) Integer churchId){
        return transactionService.getChurch(churchId);
    }


}
