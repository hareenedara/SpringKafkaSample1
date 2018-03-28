package com.samples.converter;


import com.samples.config.ConverterConfig;
import com.samples.model.IrInvoice;
import com.samples.utils.parser.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class InvoiceConverter {
    /**
     * Converts the map produced from csv by Default converter into the Ir Invoice model
     * @param body
     * @param mediaType
     * @return
     */
    @Autowired
    ConverterConfig converterConfig;

    public static final Logger log = LoggerFactory.getLogger(InvoiceConverter.class);

    public IrInvoice convert(String body, MediaType mediaType) throws Exception {
        log.info(">> inside Invoice converter");
        Object obj=parse(body,mediaType);
        IrInvoice invoice=null;
        if(obj instanceof IrInvoice){
             invoice = (IrInvoice) obj;
        }else if(obj instanceof Map){
            invoice = new IrInvoice();
            try {
                Map<String,String> csvmap=(Map<String,String>)obj;
                invoice.clientECID = Long.parseLong(csvmap.get("client_eci")== null?"0":csvmap.get("client_eci").trim());
                if(csvmap.get("invoice_status") != null)
                    invoice.invoiceState = IrInvoice.InvoiceState.valueOf(csvmap.get("invoice_status").trim());
                invoice.payerID = Long.parseLong(csvmap.get("payer_id") == null?"0":csvmap.get("payer_id").trim());
                invoice.payerIDType = IrInvoice.PayerIDType.valueOf(csvmap.get("payer_id_type").trim());
                invoice.payerAltID = csvmap.get("payer_alt_id");
                if(csvmap.get("payer_alt_id_type") !=null)
                    invoice.payerAltIDType = IrInvoice.PayerIDType.valueOf(csvmap.get("payer_alt_id_type").trim());
                invoice.invoiceReference = csvmap.get("invoice_ref");
                invoice.invoiceReferenceType = csvmap.get("invoice_ref_type");
                invoice.invoiceReference2 = csvmap.get("invoice_ref2");
                invoice.invoiceReferenceType2 = csvmap.get("invoice_ref_type2");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                if(csvmap.get("invoice_due_date") != null)
                    invoice.invoiceDueDate = csvmap.get("invoice_due_date").trim();
                if(csvmap.get("invoice_issue_date") != null)
                    invoice.invoiceIssueDate = csvmap.get("invoice_issue_date").trim();
                invoice.statementID = csvmap.get("statement_id");
                invoice.altRefID = csvmap.get("alt_ref_id");
                invoice.altRefID2 = csvmap.get("alt_ref_id2");
                invoice.currencyCode = csvmap.get("currency_code");
                if(csvmap.get("cash_final_amount") != null)
                    invoice.finalAmount = new BigDecimal(csvmap.get("cash_final_amount").trim());
                if(csvmap.get("cash_sales_tax_amount") != null)
                    invoice.salesTaxAmount = new BigDecimal(csvmap.get("cash_sales_tax_amount").trim());
                if(csvmap.get("cash_discount_amount") != null)
                    invoice.discountAmount = new BigDecimal(csvmap.get("cash_discount_amount").trim());
                invoice.discountDays = Integer.parseInt(csvmap.get("cash_discount_days") == null ?"0":csvmap.get("cash_discount_days").trim());
            }catch(Exception ex){
                log.error("Invalid field in the input record: ${ex.message}",ex);

            }
        }
        return invoice;
    }

    Object parse(String body, MediaType mediaType) throws Exception {
        Object obj =new Object();
        if(mediaType==MediaType.APPLICATION_JSON) {
            log.info(">>json parser");
            //obj= JsonParser.parse(body, converterConfig.model)
        }else if(mediaType == MediaType.TEXT_PLAIN){
            log.info(">>csv parser");
            obj= CSVParser.parse(body, converterConfig.headers);
        }else if(mediaType==MediaType.APPLICATION_XML){
            //call xml parser
        } else {
            log.info(">> anything else");
            //process anything else here.
        }

        return obj;

    }

}
