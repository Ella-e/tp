package vitalconnect.logic.parser;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import vitalconnect.commons.core.index.Index;
import vitalconnect.commons.util.StringUtil;
import vitalconnect.logic.parser.exceptions.ParseException;
import vitalconnect.model.person.contactinformation.Address;
import vitalconnect.model.person.contactinformation.Email;
import vitalconnect.model.person.contactinformation.Phone;
import vitalconnect.model.person.identificationinformation.Name;
import vitalconnect.model.person.identificationinformation.Nric;
import vitalconnect.model.person.medicalinformation.AllergyTag;
import vitalconnect.model.person.medicalinformation.Height;
import vitalconnect.model.person.medicalinformation.Weight;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index is not a non-zero unsigned integer.";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }


    /**
     * Parses a {@code String duration} into an integer representing the duration of an appointment.
     * The duration is expected to be a positive integer, representing the number of time intervals
     * (with each interval being 15 minutes) for the appointment.
     *
     * @param duration The string input representing the duration in time intervals.
     * @return An integer representing the parsed duration in time intervals.
     * @throws ParseException if the given {@code duration} is invalid, not a positive integer.
     */
    public static int parseDuration(String duration) throws ParseException {
        String trimmedDuration = duration.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedDuration)) {
            throw new ParseException("Invalid duration format. Duration must be a positive integer.");
        }
        int durationValue = Integer.parseInt(trimmedDuration);
        if (durationValue > 96) {
            throw new ParseException("Invalid duration: Duration must be a positive integer"
                    + " less than or equal to 96 (24 hours).");
        }
        return durationValue;
    }


    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String nric} into a {@code Nric}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code nric} is invalid.
     */
    public static Nric parseNric(String nric) throws ParseException {
        requireNonNull(nric);
        String trimmedName = nric.trim();
        if (!Nric.isValidNric(trimmedName)) {
            throw new ParseException(Nric.MESSAGE_CONSTRAINTS);
        }
        return new Nric(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parseEditPhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidEditPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS_EDIT);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (!Address.isValidAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseEditAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (!Address.isValidEditAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS_EDIT);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEditEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEditEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS_EDIT);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String dateTimeStr} into a {@code Date}.
     * @param dateTimeStr A string representing the date and time.
     * @return A {@code Date} object representing the date and time.
     * @throws ParseException if the given {@code String dateTimeStr} is invalid.
     */
    public static LocalDateTime parseTime(String dateTimeStr) throws ParseException {
        requireNonNull(dateTimeStr);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HHmm");
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);

            // Check if the parsed date is valid (re-parse the date to see if it remains the same)
            String roundTrip = dateTime.format(formatter);
            if (!roundTrip.equals(dateTimeStr)) {
                throw new DateTimeParseException("Invalid date time", dateTimeStr, 0);
            }

            return dateTime;
        } catch (DateTimeParseException e) {
            throw new ParseException("Invalid date time. Please ensure the date "
                    + "exists and enter in the format 'dd/MM/yyyy HHmm'.");
        }
    }

    /**
     * Parses a {@code String allergytag} into a {@code AllergyTag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code allergytag} is invalid.
     */
    public static AllergyTag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!AllergyTag.isValidTagName(trimmedTag)) {
            throw new ParseException(AllergyTag.MESSAGE_CONSTRAINTS);
        }
        return new AllergyTag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<AllergyTag>}.
     */
    public static Set<AllergyTag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<AllergyTag> allergyTagSet = new HashSet<>();
        for (String tagName : tags) {
            allergyTagSet.add(parseTag(tagName));
        }
        return allergyTagSet;
    }

    /**
     * Parses a {@code String height} into a {@code HEIGHT}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code HEIGHT} is invalid.
     */
    public static Height parseHeight(String height) throws ParseException {
        requireNonNull(height);
        String trimmedHeight = height.trim();
        if (!Height.isValidHeight(trimmedHeight)) {
            throw new ParseException(Height.MESSAGE_CONSTRAINTS);
        }
        return new Height(trimmedHeight);
    }

    /**
     * Parses a {@code String weight} into a {@code WEIGHT}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code WEIGHT} is invalid.
     */
    public static Weight parseWeight(String weight) throws ParseException {
        requireNonNull(weight);
        String trimmedWeight = weight.trim();
        if (!Weight.isValidWeight(trimmedWeight)) {
            throw new ParseException(Weight.MESSAGE_CONSTRAINTS);
        }
        return new Weight(trimmedWeight);
    }
}
