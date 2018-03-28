package com.samples.utils.parser;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVParser {
    static final String FieldSeparator = ",";
    static final Logger log = LoggerFactory.getLogger(CSVParser.class);
    /**
     * Process the input CSV record and converts it into a Map
     * @param line
     * @param csvHeader
     * @return Map
     */
    public static Object parse(String line, String csvHeader) throws Exception {
        List<String> csvHeaderList= Arrays.asList(csvHeader.split(FieldSeparator));
        List<String> csvRecord=Arrays.asList(line.split(FieldSeparator));
        Map<String, String> keyvalpairs = new HashMap<>();

        if (csvHeaderList.size()== csvRecord.size()) {
            for(int index=0;index<csvHeaderList.size();index++){
                keyvalpairs.put(csvHeaderList.get(index),csvRecord.get(index));
            }
        }else{
            log.error("CSV Headers configured and the incoming Records do not match");
            throw new Exception("CSV Headers configured and the incoming Records do not match");
        }
        return keyvalpairs;
    }
}
