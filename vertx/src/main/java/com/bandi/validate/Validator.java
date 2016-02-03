package com.bandi.validate;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.rule.ValidationResult.Level;
import org.raml.parser.visitor.RamlValidationService;

import com.bandi.log.Logger;

public class Validator {

	public static boolean isValidRAML(String ramlLocation) {
		List<ValidationResult> results = RamlValidationService.createDefault().validate(ramlLocation);
		if (CollectionUtils.isEmpty(results))
			return true;
		else {
			for (ValidationResult validationResult : results) {
				if (validationResult.getLevel() == Level.ERROR) {
					Logger.log("RAML is not valid, contains errors : " + validationResult.getMessage());
					return false;
				} else if (validationResult.getLevel() == Level.WARN) {
					Logger.log("RAML contains warnings : " + validationResult.getMessage());
					continue;
				} else if (validationResult.getLevel() == Level.INFO) {
					Logger.log("RAML validation info : " + validationResult.getMessage());
					continue;
				}
			}
		}
		return true;
	}
}
