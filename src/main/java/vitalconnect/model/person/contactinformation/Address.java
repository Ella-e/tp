package vitalconnect.model.person.contactinformation;

import static java.util.Objects.requireNonNull;
import static vitalconnect.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's address in the clinic.
 * Guarantees: immutable; is valid as declared in {@link #isValidAddress(String)}
 */
public class Address {
    public static final int MAX_LENGTH = 50;

    public static final String MESSAGE_CONSTRAINTS =
        "Addresses can take any values, and it should not be blank.\n"
          + "Max length is " + MAX_LENGTH + " characters containing spaces.";
    public static final String MESSAGE_CONSTRAINTS_EDIT =
        "Max length is " + MAX_LENGTH + " characters containing spaces.";

    /*
     * The first character of the address must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[^\\s].*";

    public final String value;

    /**
     * Constructs an {@code Address}.
     *
     * @param address A valid address.
     */
    public Address(String address) {
        requireNonNull(address);
        checkArgument(isValidEditAddress(address), MESSAGE_CONSTRAINTS);
        value = address;
    }

    public Address() {
        value = "";
    }

    public boolean isEmpty() {
        return value.equals("");
    }

    /**
     * Returns true if a given string is a valid email.
     */
    public static boolean isValidAddress(String test) {
        return test.matches(VALIDATION_REGEX) && test.length() <= MAX_LENGTH;
    }

    /**
     * Returns true if a given string is a valid email.
     */
    public static boolean isValidEditAddress(String test) {
        return (test.matches(VALIDATION_REGEX) || test.equals("")) && test.length() <= MAX_LENGTH;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Address)) {
            return false;
        }

        Address otherAddress = (Address) other;
        return value.equals(otherAddress.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
