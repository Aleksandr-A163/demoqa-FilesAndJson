package model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ShopData {
    private String name;
    private String type;
    private String isin;
    private List<String> exchanges;
    private AdditionalInfo additionalInfo;

    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }

    public String getIsin() {
        return isin;
    }

    public List<String> getExchanges() {
        return exchanges;
    }
    public AdditionalInfo getAdditionalInfo() {
        return additionalInfo;
    }

    public static class AdditionalInfo {
        private double minLotSize;
        @JsonProperty("trading_enabled")
        private Boolean tradingEnabled;
        @JsonProperty("has_options")
        private Boolean hasOptions;

        public double getMinLotSize() {
            return minLotSize;
        }
        public Boolean getTradingEnabledFlag() {

            return tradingEnabled;
        }
        public Boolean getHasOptionsFlag() {
            return hasOptions;
        }
    }

}