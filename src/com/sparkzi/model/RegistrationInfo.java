package com.sparkzi.model;

public class RegistrationInfo {

    private String gender;
    private String lowerAge;
    private String upperAge;
    private String dob;         // yyyy-mm-dd
    private String bYear;
    private String bMonth;
    private String bDate;
    private String country;
    private String city;
    private String firstName;
    private String lastName;
    private String email;
    private String password;


    public RegistrationInfo() {
        // TODO Auto-generated constructor stub
    }


    public RegistrationInfo(String gender, String lowerAge, String upperAge, String dob, String country, String city,
            String firstName, String lastName, String email, String password) {
        this.gender = gender;
        this.lowerAge = lowerAge;
        this.upperAge = upperAge;
        this.dob = dob;
        this.country = country;
        this.city = city;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }


    public String getGender() {
        return gender;
    }


    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getLowerAge() {
        return lowerAge;
    }


    public void setLowerAge(String lowerAge) {
        this.lowerAge = lowerAge;
    }


    public String getUpperAge() {
        return upperAge;
    }


    public void setUpperAge(String upperAge) {
        this.upperAge = upperAge;
    }


    public String getDob() {
        return dob;
    }


    public void setDob(String dob) {
        this.dob = dob;
        this.bYear = dob.substring(0, 4);
        this.bMonth = dob.substring(5, 7);
        this.bDate = dob.substring(8, 10);
    }


    public String getbYear() {
        return bYear;
    }


    public void setbYear(String bYear) {
        this.bYear = bYear;
    }


    public String getbMonth() {
        return bMonth;
    }


    public void setbMonth(String bMonth) {
        this.bMonth = bMonth;
    }


    public String getbDate() {
        return bDate;
    }


    public void setbDate(String bDate) {
        this.bDate = bDate;
    }


    public String getCountry() {
        return country;
    }


    public void setCountry(String country) {
        this.country = country;
    }


    public String getCity() {
        return city;
    }


    public void setCity(String city) {
        this.city = city;
    }


    public String getFirstName() {
        return firstName;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getLastName() {
        return lastName;
    }


    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }
    
    
    
    
}