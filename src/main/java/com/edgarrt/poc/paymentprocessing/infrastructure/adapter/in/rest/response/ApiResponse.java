package com.edgarrt.poc.paymentprocessing.infrastructure.adapter.in.rest.response;

public record ApiResponse<T>(String code, String message, T data) {
    public static <T> ApiResponse<T> ok(T data) { return new ApiResponse<>("OK", "Operation completed", data); }
}
