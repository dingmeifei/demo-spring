package com.dmf.resource1.module;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AddOneReq implements Serializable {

    private static final long serialVersionUID = -4735838547409368763L;

    @NotNull(message = "金额不能为空")
    private Integer amount;

}
