package py.com.jmbr.mcs.icejas.controller;

import com.lowagie.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.com.jmbr.java.commons.beans.mcs.icejas.*;
import py.com.jmbr.java.commons.context.OperationAllow;
import py.com.jmbr.java.commons.context.SecurityAccess;
import py.com.jmbr.java.commons.domain.mcs.icejas.Church;
import py.com.jmbr.java.commons.domain.mcs.icejas.TransactionType;
import py.com.jmbr.mcs.icejas.annotation.IcejasSecurityAccess;
import py.com.jmbr.mcs.icejas.constant.TransactionConstant;
import py.com.jmbr.mcs.icejas.service.TransactionService;

import javax.validation.Valid;
import java.io.IOException;
import java.sql.Date;

@RestController
@RequestMapping(value = "transactions/${version}")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;
    @PostMapping(value = "/")
    @IcejasSecurityAccess(operation = OperationAllow.VERIFY_TOKEN)
    public TransactionPostResData addTransactions(
            @RequestHeader(value = TransactionConstant.API_KEY,required = true) String apiKey,
            @RequestHeader(value = TransactionConstant.AUTHORIZATION,required = true) String accessToken,
            @RequestBody @Valid TransactionPostReqData req){
        return transactionService.addTransactions(req.getData());
    }

    @PutMapping("/")
    @IcejasSecurityAccess(operation = OperationAllow.VERIFY_TOKEN)
    @Operation(summary = "update a transactions ",description = "update a transaction ")
    public TransactionPutResData udpateTransactions(
            @RequestHeader(value = TransactionConstant.API_KEY,required = true) String apiKey,
            @RequestHeader(value = TransactionConstant.AUTHORIZATION,required = true) String accessToken,@RequestBody @Valid TransactionPostReqData req){
        return transactionService.updateTransaction(req.getData());
    }

    @DeleteMapping("/")
    @IcejasSecurityAccess(operation = OperationAllow.VERIFY_TOKEN)
    @Operation(summary = "get transactions ",description = "Delete transactions that belongs a church ")
    public TransactionDeleteResData deleteTransaction(@RequestHeader(value = TransactionConstant.API_KEY,required = true) String apiKey,
                                                      @RequestHeader(value = TransactionConstant.AUTHORIZATION,required = true) String accessToken,@RequestParam(value = "transactionId",required = true)Integer transactionId,
                                                      @RequestParam(value = "churchId") Integer churchId){
        return transactionService.deleteTransaction(transactionId,churchId);
    }

    @GetMapping("/")
    @IcejasSecurityAccess(operation = OperationAllow.VERIFY_TOKEN)
    @Operation(summary = "get transactions ",description = "Get all transactions that belongs a church ")
    public TransactionDetailGetResData getTransactions(
            @RequestHeader(value = TransactionConstant.API_KEY,required = true) String apiKey,
            @RequestHeader(value = TransactionConstant.AUTHORIZATION,required = true) String accessToken,
            @RequestParam(value = "churchId",required = true)Integer churchId,
            @RequestParam(value = "startDate",required = false) String startDate,
            @RequestParam(value = "endDate",required = false) String endDate,
            @RequestParam(value = "activiteType",required = false)Integer activiteType,
            @RequestParam(value = "transactionType",required = false)String transactionType
            ){

        return transactionService.getTransactionDetails(churchId,startDate,endDate,activiteType,transactionType);
    }
    @GetMapping("/types")
    @IcejasSecurityAccess(operation = OperationAllow.VERIFY_TOKEN)
    @Operation(summary = "get transactions types",description = "Get all transactions types ")
    public TransactionTypesGetResData getTransactionTypes(@RequestHeader(value = TransactionConstant.API_KEY,required = true) String apiKey,
                                                          @RequestHeader(value = TransactionConstant.AUTHORIZATION,required = true) String accessToken){
        return transactionService.getTransactionTypes();
    }
    @GetMapping("/church")
    @IcejasSecurityAccess(operation = OperationAllow.VERIFY_TOKEN)
    @Operation(summary = "get church",description = "Get church details ")
    public Church getChurch(@RequestHeader(value = TransactionConstant.API_KEY,required = true) String apiKey,
                            @RequestHeader(value = TransactionConstant.AUTHORIZATION,required = true) String accessToken,@RequestParam(value = "church_id",required = true) Integer churchId){
        return transactionService.getChurch(churchId);
    }

    @PostMapping("/types")
    @IcejasSecurityAccess(operation = OperationAllow.VERIFY_TOKEN)
    @Operation(summary = "add transaction type",description = "Add a new transaction type ")
    public TransactionTypesPostRestData addTransactionType(@RequestHeader(value = TransactionConstant.API_KEY,required = true) String apiKey,
                                                           @RequestHeader(value = TransactionConstant.AUTHORIZATION,required = true) String accessToken,@RequestBody TransactionType transactionType){
        return transactionService.addTransactionType(transactionType);
    }


    @GetMapping("/report")
    @IcejasSecurityAccess(operation = OperationAllow.VERIFY_TOKEN)
    @Operation(summary = "report balances",description = "Report sum total balances every month ")
    public TransactionReportGetResData getReportMonth(@RequestHeader(value = TransactionConstant.API_KEY,required = true) String apiKey,
                                                      @RequestHeader(value = TransactionConstant.AUTHORIZATION,required = true) String accessToken,
            @RequestParam(value = "churchId",required = true) Integer churchId
            ){
        return transactionService.getReportMonth(churchId);
    }

    @GetMapping("/summary")
    @IcejasSecurityAccess(operation = OperationAllow.VERIFY_TOKEN)
    @Operation(summary = "report balances",description = "Report balances every month ")
    public MonthSummaryGetResData getSummaryMonth(
            @RequestHeader(value = TransactionConstant.API_KEY,required = true) String apiKey,
            @RequestHeader(value = TransactionConstant.AUTHORIZATION,required = false) String accessToken,
            @RequestParam(value = "startDate",required = false) String startDate,
            @RequestParam(value = "endDate",required = false) String endDate,
            @RequestParam(value = "churchId",required = true) Integer churchId
    ){

        return transactionService.getMonthSummary(startDate,endDate,churchId);

    }
    @PostMapping(value = "/pdf",produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]>  getReportPDF(@RequestParam(value = "startDate",required = false) String startDate,
                                                         @RequestParam(value = "endDate",required = false) String endDate,
                                                         @RequestParam(value = "churchId",required = true) Integer churchId,
                                                        @RequestParam(value = "trimester",required = true) Integer trimester) throws IOException, DocumentException {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=reporte.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(transactionService.getReportPDF(startDate,endDate,trimester,churchId));
    }


}
