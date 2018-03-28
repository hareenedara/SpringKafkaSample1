package com.samples.model;


import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


public class IrInvoice {

    @Min(value=1L)
    public long clientECID;
    @NotNull
    public InvoiceState invoiceState;
    @Min(value=1L)
    public long payerID;
    public PayerIDType payerIDType;
    public String payerAltID;
    public PayerIDType payerAltIDType;
    @NotEmpty
    public String invoiceReference;
    public String invoiceReferenceType;
    public String invoiceReference2;
    public String invoiceReferenceType2;
    public String invoiceDueDate;
    public String invoiceIssueDate;
    public String statementID;
    public String altRefID;
    public String altRefID2;
    @NotEmpty
    public String currencyCode;
    @NotNull
    @NumberFormat(style= NumberFormat.Style.CURRENCY)
    public BigDecimal finalAmount;
    @NotNull
    public BigDecimal salesTaxAmount;
    public BigDecimal discountAmount;
    public int discountDays;

    public enum InvoiceState{
       OPEN, AMEND, CLOSED
    }
    public enum PayerIDType{
        ECI,TIN
    }

}
