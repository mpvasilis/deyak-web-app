package gr.uowm.deyakwebapp.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.util.Date;

@Entity
public class Data {
    @Id
    private Long id;
    private int customerNo;
    private int serialNo;
    private float e1;
    private float v1;
    private float t1;
    private float t2;
    private int infoCode;
    private int operatingHours;
    private Date  date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(int customerNo) {
        this.customerNo = customerNo;
    }

    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    public float getE1() {
        return e1;
    }

    public void setE1(float e1) {
        this.e1 = e1;
    }

    public float getV1() {
        return v1;
    }

    public void setV1(float v1) {
        this.v1 = v1;
    }

    public float getT1() {
        return t1;
    }

    public void setT1(float t1) {
        this.t1 = t1;
    }

    public float getT2() {
        return t2;
    }

    public void setT2(float t2) {
        this.t2 = t2;
    }

    public int getInfoCode() {
        return infoCode;
    }

    public void setInfoCode(int infoCode) {
        this.infoCode = infoCode;
    }

    public int getOperatingHours() {
        return operatingHours;
    }

    public void setOperatingHours(int operatingHours) {
        this.operatingHours = operatingHours;
    }

    public Date  getDate() {
        return date;
    }

    public void setDate(Date  date) {
        this.date = date;
    }

    public String getClient() {
        return String.valueOf(customerNo);
    }
}