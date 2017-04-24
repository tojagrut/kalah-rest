package com.backbase.kalah.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.backbase.kalah.model.Pit;
import com.backbase.kalah.util.Constants;

/**
 * Validator class for Pit
 * Created by tojagrut
 */
public class PitValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return Pit.class.equals(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Pit pit = (Pit) target;

        // validate Pit id
        if (pit.getId() < 1 || pit.getId() > Constants.MAX_PITS) {
            errors.rejectValue("id", "id.Invalid", "Pit id should be between 1 to 6");
        }
    }
}
