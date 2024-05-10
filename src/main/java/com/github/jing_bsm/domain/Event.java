package com.github.jing_bsm.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private String userId;
    private String instanceId;
    private String customerId;
    private String message;
}
