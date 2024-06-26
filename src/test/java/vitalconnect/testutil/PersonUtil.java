package vitalconnect.testutil;

import static vitalconnect.logic.parser.CliSyntax.PREFIX_ALLERGYTAG;
import static vitalconnect.logic.parser.CliSyntax.PREFIX_NAME;
import static vitalconnect.logic.parser.CliSyntax.PREFIX_NRIC;

import vitalconnect.logic.commands.AddCommand;
import vitalconnect.model.person.Person;

/**
 * A utility class for Person.
 */
public class PersonUtil {

    /**
     * Returns an add command string for adding the {@code person}.
     */
    public static String getAddCommand(Person person) {
        return AddCommand.COMMAND_WORD + " " + getPersonDetails(person);
    }

    /**
     * Returns the part of command string for the given {@code person}'s details.
     */
    public static String getPersonDetails(Person person) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME + person.getIdentificationInformation().getName().fullName + " ");
        sb.append(PREFIX_NRIC + person.getIdentificationInformation().getNric().nric + " ");
        person.getMedicalInformation().getAllergyTag().stream().forEach(
            s -> sb.append(PREFIX_ALLERGYTAG + s.tagName + " ")
        );
        return sb.toString();
    }
}
