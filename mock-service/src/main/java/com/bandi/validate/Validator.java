package com.bandi.validate;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.rule.ValidationResult.Level;
import org.raml.parser.visitor.RamlValidationService;

import com.bandi.log.Logger;

public class Validator {

	public static boolean isValidRAML(String ramlLocation) {
		List<ValidationResult> results = RamlValidationService.createDefault().validate(ramlLocation);
		boolean isValid = true;
		
		if (CollectionUtils.isEmpty(results))
			return isValid;
		else {
			for (ValidationResult validationResult : results) {
				if (validationResult.getLevel() == Level.ERROR) {
					Logger.error("RAML is not valid, contains errors : " + validationResult.getMessage());
					isValid = false;
				} else if (validationResult.getLevel() == Level.WARN) {
					Logger.error("RAML contains warnings : " + validationResult.getMessage());
					continue;
				} else if (validationResult.getLevel() == Level.INFO) {
					Logger.error("RAML validation info : " + validationResult.getMessage());
					continue;
				}
			}
		}
		return isValid;
	}
}
