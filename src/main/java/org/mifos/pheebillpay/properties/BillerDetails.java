package org.mifos.pheebillpay.properties;

public class BillerDetails {

    String id;
    String biller;
    String billerAccount;
    String billerCategory;

    @Override
    public String toString() {
        return "BillerDetails{" + "id='" + id + '\'' + ", biller='" + biller + '\'' + ", billerAccount='" + billerAccount + '\''
                + ", billerCategory='" + billerCategory + '\'' + '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBiller() {
        return biller;
    }

    public void setBiller(String biller) {
        this.biller = biller;
    }

    public String getBillerAccount() {
        return billerAccount;
    }

    public void setBillerAccount(String billerAccount) {
        this.billerAccount = billerAccount;
    }

    public String getBillerCategory() {
        return billerCategory;
    }

    public void setBillerCategory(String billerCategory) {
        this.billerCategory = billerCategory;
    }

}
