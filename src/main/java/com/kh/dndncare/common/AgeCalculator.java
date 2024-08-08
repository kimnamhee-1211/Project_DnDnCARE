package com.kh.dndncare.common;

import java.sql.Date;
import java.time.LocalDate;


public class AgeCalculator {

	
	public static int calculateAge(Date birthDate) {
		
		LocalDate today = LocalDate.now();
		
		LocalDate birth = birthDate.toLocalDate();

		int currentYear = today.getYear();
		int birthYear = birth.getYear();
		
        int age = currentYear - birthYear;
        
        if (today.getMonthValue() < birth.getMonthValue() || 
                (today.getMonthValue() == birth.getMonthValue() && today.getDayOfMonth() < birth.getDayOfMonth())) {
                age--;
            }
        
        return age;
		
	}
	
	
	
}
