package com.ayman.transactionservice.constant;
public class EmailConstant {
    public static final String REQUEST_EMAIL_SUBJECT = "Request For %s";
    public static final String REQUEST_APPROVAL_EMAIL_SUBJECT = "Request Approved";
    public static final String REQUEST_DENIED_EMAIL_SUBJECT = "Request Denied";

    public static final String BUYER_APPROVAL_MESSAGE = "Your request to the property %s has been approved by %s";
    public static final String BUYER_DENIAL_MESSAGE = "Your request to the property %s has been denied by %s \n%s";
    public static final String SELLER_REQUEST_MESSAGE = "You received a new request from %s to the property %s";
    public static final String BUYER_REQUEST_MESSAGE = "Your request to the property %s have been delivered successfully";
    public static final String BROKER_REQUEST_MESSAGE = "New request from %s to the property %s";
}
