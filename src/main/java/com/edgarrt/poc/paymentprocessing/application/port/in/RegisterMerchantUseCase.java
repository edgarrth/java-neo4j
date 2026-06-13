package com.edgarrt.poc.paymentprocessing.application.port.in;

import com.edgarrt.poc.paymentprocessing.domain.model.Merchant;

public interface RegisterMerchantUseCase { Merchant register(Merchant merchant); }
