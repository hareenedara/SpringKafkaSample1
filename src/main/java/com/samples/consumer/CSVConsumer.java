package com.samples.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samples.config.KafkaConfig;
import com.samples.converter.InvoiceConverter;
import com.samples.model.IrInvoice;
import com.samples.service.SenderService;
import com.samples.utils.customexceptions.KafkaSendException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@Component
public class CSVConsumer {
    @Autowired
    private InvoiceConverter invoiceConverter;
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private KafkaConfig kafkaConfig;
    @Autowired
    private SenderService senderService;

    @NotNull
    @Async
    public Future<Boolean> consumeMsg(@NotNull ConsumerRecord<String,String> record) throws KafkaSendException {
        try {
            System.out.printf("Thread = %s, offset = %d, key = %s, value = %s%n", Thread.currentThread().getName(), record.offset(), record.key(), record.value());
            IrInvoice irInvoice = invoiceConverter.convert(record.value(), MediaType.TEXT_PLAIN);
            String json = mapper.writeValueAsString(irInvoice);
            senderService.send(kafkaConfig.jsonTopic,record.key(),json);
        }catch (Exception ex) {
            KafkaSendException kse = new KafkaSendException();
            kse.setVal(record.value());
            kse.setKey(record.key());
            kse.setErrorMsg(ex.getMessage());
            throw kse;
        }
        return new AsyncResult<>(true);
    }
}
