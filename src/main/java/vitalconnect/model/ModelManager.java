package vitalconnect.model;

import static java.util.Objects.requireNonNull;
import static vitalconnect.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import vitalconnect.commons.core.GuiSettings;
import vitalconnect.commons.core.LogsCenter;
import vitalconnect.commons.core.index.Index;
import vitalconnect.model.person.Person;
import vitalconnect.model.person.contactinformation.ContactInformation;
import vitalconnect.model.person.identificationinformation.IdentificationInformation;
import vitalconnect.model.person.identificationinformation.Nric;
import vitalconnect.model.person.medicalinformation.MedicalInformation;


/**
 * Represents the in-memory model of the clinic data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final Clinic clinic;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final ObservableList<Appointment> appointments;
    private ObservableList<Appointment> foundApt;
    private Predicate<Person> currentPredicate;


    /**
     * Initializes a ModelManager with the given clinic and userPrefs.
     */
    public ModelManager(ReadOnlyClinic clinic, ReadOnlyUserPrefs userPrefs, List<Appointment> loadedAppointments) {
        requireAllNonNull(clinic, userPrefs);

        logger.fine("Initializing with clinic: " + clinic + " and user prefs " + userPrefs);

        this.clinic = new Clinic(clinic);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.clinic.getPersonList());
        this.appointments = FXCollections.observableArrayList();
        this.foundApt = FXCollections.observableArrayList();
        this.currentPredicate = PREDICATE_SHOW_ALL_PERSONS;

        if (loadedAppointments != null) {
            this.appointments.setAll(loadedAppointments);

        }
    }

    public ModelManager() {
        this(new Clinic(), new UserPrefs(), new ArrayList<>());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getClinicFilePath() {
        return userPrefs.getClinicFilePath();
    }

    @Override
    public void setClinicFilePath(Path clinicFilePath) {
        requireNonNull(clinicFilePath);
        userPrefs.setClinicFilePath(clinicFilePath);
    }

    /**
     * Adds the given appointment to the clinic.
     *
     * @param appointment The appointment to add.
     */
    @Override
    public void addAppointment(Appointment appointment) {
        requireNonNull(appointment);
        appointments.add(appointment);
        FXCollections.sort(appointments, Comparator.comparing(Appointment::getDateTime));
    }

    /**
     * Replaces the current list of appointments with the provided list.
     *
     * @param appointments The list of appointments to set.
     */
    @Override
    public void setAppointments(List<Appointment> appointments) {
        this.appointments.setAll(appointments);
    }

    /**
     * Returns an unmodifiable view of the list of appointments.
     *
     * @return An unmodifiable view of the list of appointments.
     */
    @Override
    public ObservableList<Appointment> getFilteredAppointmentList() {
        FXCollections.sort(appointments, Comparator.comparing(Appointment::getDateTime));
        return appointments;
    }

    /**
     * Deletes the specified appointment from the clinic.
     *
     * @param appointment The appointment to delete.
     */
    @Override
    public void deleteAppointment(Appointment appointment) {
        appointments.remove(appointment);
    }

    /**
     * Deletes all appointments from the clinic.
     *
     */
    @Override
    public void clearAppointments() {
        appointments.clear();
    }

    @Override
    public List<Appointment> getConflictingAppointments(Appointment newAppointment) {
        return getFilteredAppointmentList().stream()
                .filter(existingAppointment ->
                        newAppointment.getDateTime().isBefore(existingAppointment.getEndDateTime())
                                && newAppointment.getEndDateTime().isAfter(existingAppointment.getDateTime()))
                .collect(Collectors.toList());
    }
    @Override
    public ObservableList<Appointment> findAppointmentsByNric(Nric nric) {
        requireNonNull(nric);
        List<Appointment> foundAppointments = appointments.stream()
                .filter(appointment -> appointment.getPatientIc().equals(nric.toString()))
                .collect(Collectors.toList());

        foundApt = FXCollections.observableArrayList(foundAppointments);
        return getFoundAppointmentList();
    }
    @Override
    public ObservableList<Appointment> getFoundAppointmentList() {
        FXCollections.sort(foundApt, Comparator.comparing(Appointment::getDateTime));
        return foundApt;
    }
    @Override
    public List<Appointment> getConflictingAppointmentsForExistingApt(Index index, Appointment newAppointment) {
        ArrayList<Appointment> appointments = new ArrayList<>(getFilteredAppointmentList());
        appointments.remove(index.getZeroBased());
        return appointments.stream()
          .filter(existingAppointment ->
            newAppointment.getDateTime().isBefore(existingAppointment.getEndDateTime())
              && newAppointment.getEndDateTime().isAfter(existingAppointment.getDateTime()))
          .collect(Collectors.toList());
    }

    @Override
    public void setClinic(ReadOnlyClinic clinic) {
        this.clinic.resetData(clinic);
    }

    @Override
    public ReadOnlyClinic getClinic() {
        return clinic;
    }

    @Override
    public ReadOnlyClinic getClinicCopy() {
        ReadOnlyClinic copy = new Clinic(clinic);
        return copy;
    }

    @Override
    public List<Appointment> getAppointmentsCopy() {
        List<Appointment> copy = new ArrayList<Appointment>(appointments);
        return copy;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return clinic.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        clinic.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        clinic.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        clinic.setPerson(target, editedPerson);
    }
    /**
     * Checks if a person with the specified name exists in the clinic.
     *
     * @param name The name of the person to check for existence.
     * @return true if there is at least one person in the clinic with the specified name, ignoring case.
     */
    @Override
    public boolean doesPersonExist(String name) {
        requireNonNull(name);
        return filteredPersons.stream()
                .anyMatch(person -> person.getIdentificationInformation().getName().fullName.equalsIgnoreCase(name));
    }
    /**
     * Checks if a person with the specified National Registration Identity Card (NRIC) exists in the clinic.
     *
     * @param ic The NRIC of the person to check for existence.
     * @return true if there is at least one person in the clinic with the specified NRIC, ignoring case.
     */
    @Override
    public boolean doesIcExist(String ic) {
        requireNonNull(ic);
        return filteredPersons.stream()
                .anyMatch(person -> person.getIdentificationInformation().getNric().nric.equalsIgnoreCase(ic));
    }
    /**
     * Finds and returns the person in the clinic whose NRIC matches the specified NRIC.
     *
     * @param nric The NRIC of the person to find.
     * @return The person with the specified NRIC or null if no such person exists in the clinic.
     */
    @Override
    public Person findPersonByNric(Nric nric) {
        requireNonNull(nric);
        return clinic.findPersonByNric(nric);
    }

    /**
     * Finds and returns the person in the clinic whose identification info matches the specified identification info.
     *
     * @param info The identification info of the person to find.
     * @return The person with the specified identification info or null if no such person exists in the clinic.
     */
    @Override
    public Person findPersonByNric(IdentificationInformation info) {
        requireNonNull(info);
        return clinic.findPersonByNric(info);
    }

    /**
     * Updates the contact information of the person in the clinic.
     * @param nric Nric of the person to be updated
     * @param contactInformation New contact information of the person
     */
    @Override
    public void updatePersonContactInformation(Nric nric, ContactInformation contactInformation) {
        Person person = clinic.findPersonByNric(nric);
        Person personToUpdate = person.copyPerson();
        personToUpdate.setContactInformation(contactInformation);
        setPerson(person, personToUpdate);
    }

    @Override
    public void updatePersonMedicalInformation(Nric nric, MedicalInformation medicalInformation) {
        Person person = clinic.findPersonByNric(nric);
        Person personToUpdate = person.copyPerson();
        personToUpdate.setMedicalInformation(medicalInformation);
        setPerson(person, personToUpdate);

    }

    @Override
    public void updateAppointment(Index index, Appointment appointmentToSave) {
        appointments.set(index.getZeroBased(), appointmentToSave);
        FXCollections.sort(appointments, Comparator.comparing(Appointment::getDateTime));
    }

    public void setCurrentPredicate(Predicate<Person> predicate) {
        currentPredicate = predicate;
    }

    public Predicate<Person> getCurrentPredicate() {
        return currentPredicate;
    }


    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedClinic}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    /**
     * Checks for equality with another object. Returns true if the other object is also a ModelManager
     * and has the same clinic and user preferences data.
     *
     * @param other The other object to compare against.
     * @return True if both objects are of the same class and contain the same data, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager)) {
            return false;
        }

        ModelManager otherModelManager = (ModelManager) other;
        if (clinic.equals(otherModelManager.clinic)) {
            if (userPrefs.equals(otherModelManager.userPrefs)) {
                if (filteredPersons.equals(otherModelManager.filteredPersons)) {
                    return true;
                }
            }
        }

        return clinic.equals(otherModelManager.clinic)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons);
    }

}
