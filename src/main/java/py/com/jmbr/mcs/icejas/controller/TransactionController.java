package py.com.jmbr.mcs.icejas.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import py.com.jmbr.java.commons.beans.mcs.icejas.*;
import py.com.jmbr.java.commons.domain.mcs.icejas.Church;
import py.com.jmbr.java.commons.domain.mcs.icejas.TransactionType;
import py.com.jmbr.mcs.icejas.service.TransactionService;

import javax.validation.Valid;
import java.sql.Date;

@RestController
@RequestMapping(value = "transactions/${version}")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;
    @PostMapping(value = "/")
    public TransactionPostResData addTransactions(@RequestBody @Valid TransactionPostReqData req){
        return transactionService.addTransactions(req.getData());
    }

    @PutMapping("/")
    @Operation(summary = "udpate a transactions ",description = "udpate a transaction ")
    public TransactionPutResData udpateTransactions(@RequestBody @Valid TransactionPostReqData req){
        return transactionService.updateTransaction(req.getData());
    }
    @GetMapping("/")
    @Operation(summary = "get transactions ",description = "Get all transactions that belongs a church ")
    public TransactionDetailGetResData getTransactions(
            @RequestParam(value = "churchId",required = true)Integer churchId,
            @RequestParam(value = "startDate",required = false) String startDate,
            @RequestParam(value = "endDate",required = false) String endDate,
            @RequestParam(value = "activiteType",required = false)Integer activiteType,
            @RequestParam(value = "transactionType",required = false)String transactionType
            ){

        return transactionService.getTransactionDetails(churchId,startDate,endDate,activiteType,transactionType);
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

    @PostMapping("/types")
    @Operation(summary = "add transaction type",description = "Add a new transaction type ")
    public TransactionTypesPostRestData addTransactionType(@RequestBody TransactionType transactionType){
        return transactionService.addTransactionType(transactionType);
    }

    @PostMapping("/closed")
    @Operation(summary = "closed month",description = "Close balance of the current month ")
    public TransactionMonthClosedPostResData closedMonth(
            @RequestParam(value = "userId",required = true) Integer userId,
            @RequestParam(value = "month",required = true)Date closedDate
            ){
        return transactionService.closedMonth(userId,closedDate);
    }

    @GetMapping("/report")
    @Operation(summary = "report balances",description = "Report balances every month ")
    public TransactionReportGetResData getReportMonth(
            @RequestParam(value = "churchId",required = true) Integer churchId
            ){
        return transactionService.getReportMonth(churchId);
    }



}
