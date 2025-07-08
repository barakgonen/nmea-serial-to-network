package com.example.global;

import com.opencsv.bean.CsvBindByName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CsvMessage {
    @CsvBindByName(writeLocale = "timeStamp")
    long timeStamp;
    @CsvBindByName(writeLocale = "payload")
    String payload;
}
