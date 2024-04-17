package com.amway.pojo.payment;



import com.drew.lang.annotations.NotNull;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class AddBalanceReq {


public String accountId;

public String accountType;

public String currency;

public String entryType;

public int amountValue;

public int availableBalance;

public Source source;

@Data
@Builder
public static class Source{
    public String system;
    public String refId;
}
}


