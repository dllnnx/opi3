package validation;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Validator {
    private double x, y, r;

    private boolean validateX() {
        return x >= -3 && x <= 5;
    }

    private boolean validateY() {
        return y >= -3 && y <= 3;
    }

    private boolean validateR() {
        return r >= 0.1 && r <= 5;
    }

    public boolean validateDot() {
        return validateX() && validateY() && validateR();
    }
}
