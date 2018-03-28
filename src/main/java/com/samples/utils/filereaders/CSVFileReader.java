package com.samples.utils.filereaders;

import com.samples.config.KafkaConfig;
import com.samples.service.SenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by i681336 on 8/31/2017.
 */
@Component
public class CSVFileReader {

    public static final Logger log = LoggerFactory.getLogger(CSVFileReader.class);
    @Autowired
    SenderService senderService;
    @Autowired
    KafkaConfig kafkaConfig;

    public void readLines() throws IOException, InterruptedException {
        String filename = "I:/tmp/test1.txt.bak";

        long t1 = System.currentTimeMillis();

        sendMessages(filename);
        log.info("TimeTaken (ms): "+(System.currentTimeMillis()-t1));
        log.info("Done");
    }

    public void sendMessages(String filename) throws IOException, InterruptedException {
        String msg = "";
        int counter =1;
        BufferedReader reader=null;
        try {
            reader = new BufferedReader(new FileReader(filename));
            while ((msg = reader.readLine()) != null) {
                if(msg.trim().length()<1)
                    continue;
                if(msg.startsWith("id"))
                    continue;
                senderService.send(kafkaConfig.csvTopic,String.valueOf(counter++),msg.trim() );
                log.info(msg);
                Thread.sleep(1);
                if(counter >1000000)
                    return;
            }
        }finally{
            if(reader != null)
                reader.close();
        }

    }



}
