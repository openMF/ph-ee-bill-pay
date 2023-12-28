package org.mifos.pheeBillPay.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Bill {

    private String billerId;
    private String billerName;
    private String billStatus;
    private String dueDate;
    private String amountonDueDate;

    private String amountAfterDueDate;

    public String getBillerId() {
        return billerId;
    }

    public void setBillerId(String billerId) {
        this.billerId = billerId;
    }

    public String getBillerName() {
        return billerName;
    }

    public void setBillerName(String billerName) {
        this.billerName = billerName;
    }

    public String getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getAmountonDueDate() {
        return amountonDueDate;
    }

    public void setAmountonDueDate(String amountonDueDate) {
        this.amountonDueDate = amountonDueDate;
    }

    public String getAmountAfterDueDate() {
        return amountAfterDueDate;
    }

    public void setAmountAfterDueDate(String amountAfterDueDate) {
        this.amountAfterDueDate = amountAfterDueDate;
    }

    @Override
    public String toString() {
        return "Bill{" + "billerId='" + billerId + '\'' + ", billerName='" + billerName + '\'' + ", billStatus='" + billStatus + '\''
                + ", dueDate='" + dueDate + '\'' + ", amountonDueDate='" + amountonDueDate + '\'' + ", amountAfterDueDate='"
                + amountAfterDueDate + '\'' + '}';
    }
}
