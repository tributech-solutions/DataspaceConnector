package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ContractRuleDesc extends BaseDescription<ContractRule> {
    private String title;
    private String rule;
}