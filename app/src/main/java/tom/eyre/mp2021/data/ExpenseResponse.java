package tom.eyre.mp2021.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ExpenseResponse {

    @JsonProperty(value = "Amount Paid")
    private double amountPaid;
    @JsonProperty(value = "Status")
    private String status;
    @JsonProperty(value = "Amount Claimed")
    private double amountClaimed;
    @JsonProperty(value = "Supply Month")
    private Integer supplyMonth;
    @JsonProperty(value = "Category")
    private String category;
    @JsonProperty(value = "Mileage")
    private Integer mileage;
    @JsonProperty(value = "Claim No.")
    private String claimNo;
    @JsonProperty(value = "Amount Not Paid")
    private double amountNotPaid;
    @JsonProperty(value = "Nights")
    private Integer nights;
    @JsonProperty(value = "Expense Type")
    private String expenseType;
    @JsonProperty(value = "Supply Period")
    private Integer supplyPeriod;
    @JsonProperty(value = "From")
    private String traveledFrom;
    @JsonProperty(value = "MP's Constituency")
    private String mpsConstituency;
    @JsonProperty(value = "Amount Repaid")
    private double amountRepaid;
    @JsonProperty(value = "Date")
    private String date;
    @JsonProperty(value = "Travel")
    private String travelClass;
    @JsonProperty(value = "Reason If Not Paid")
    private String reasonIfNotPaid;
    @JsonProperty(value = "Details")
    private String details;
    @JsonProperty(value = "Journey Type")
    private String journeyType;
    @JsonProperty(value = "Year")
    private Object year;
    @JsonProperty(value = "Short Description")
    private String shortDescription;
    @JsonProperty(value = "MP's Name")
    private String mpsName;
    @JsonProperty(value = "To")
    private String traveledTo;
}
