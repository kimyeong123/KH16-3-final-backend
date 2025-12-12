package com.kh.final3.domain.enums;

public enum ProductStatus {
    
	REGISTRATION("REGISTRATION"),
    
	BIDDING("BIDDING"),
    
	ENDED("ENDED"),
    
	SHIPPED("SHIPPED"),
	
	COMPLETED("COMPLETED"),
	
	DEACTIVATED("DEACTIVATED");
    
    private final String status;

    ProductStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}