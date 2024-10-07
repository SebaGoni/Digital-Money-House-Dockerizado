package com.DMH.accountsservice.feignCustomExceptions;

import feign.codec.ErrorDecoder;

public class FeignConfig {

    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
}
