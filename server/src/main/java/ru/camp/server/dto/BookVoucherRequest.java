package ru.camp.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookVoucherRequest {
    private Long childId;
    private Long sessionId;
} 