package basics;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Flight implements Comparable<Flight> {

    // --------------- ΓΝΩΡΙΣΜΑΤΑ ----------------------------------------
    private final String code;
    private final String departureTo;
    private final String via;
    private FlightStatus status;
    private Date initialSchedDeparture; // προγρ. ώρα από αεροπ. εταιρεία
    private Date scheduledTimeSlot;  // προγρ. ώρα (slot) από αεροδρόμιο
    private Date expectedTimeSlot; /* τελευτ. προγρ. ώρα (slot) για
                                      πτήσεις που είχαν καθυστέρηση  */
    private final Airline airline; // αεροπορική εταιρεία όπου ανήκει

    // ------------ ΜΕΘΟΔΟΙ ΠΡΟΣΒΑΣΗΣ-ΤΡΟΠΟΠΟΙΗΣΗΣ -----------------------
    public String getCode() {
        return code;
    }

    public String getDepartureTo() {
        return departureTo;
    }

    public String getVia() {
        return via;
    }

    public FlightStatus getStatus() {
        return status;
    }

    /* Εδώ γίνεται ο συντονισμός για τυχόν ενέργειες
     * που πρέπει να γίνονται όταν συμβαίνουν μεταβάσεις
     * από μια κατάσταση σε άλλη. (state transion diagram)
     */
    public void setStatus(FlightStatus status) {
        // εάν γίνεται μετάβαση σε κατάσταση Delayed
        if (status == FlightStatus.Delayed) {
            /* πρώτα ελέγχουμε εάν είναι επιτρεπτή (δηλ. μόνο εάν η
               υπάρχουσα κατάσταση είναι OnTime ή Delayed */
            if ((this.status == FlightStatus.OnTime) ||
                (this.status == FlightStatus.Delayed)) {
                // γίνεται η μετάβαση της κατάστασης και
                this.status = status;
                /* καλούμε το αεροδρόμιο (διαχειριστή) να δώσει
                   νέο time slot στην πτήση */
                this.airline.getAirport().getSlotForDelayedFlight(this);
            }
        } else { /* η μετάβαση στις άλλες καταστάσεις χωρίς
                    επιπλέον ενέργειες */
            this.status = status;
        }
    }
    
    public Date getInitialSchedDeparture() {
        return initialSchedDeparture;
    }

    public void setInitialSchedDeparture(Date initialSchedDeparture) {
        this.initialSchedDeparture = initialSchedDeparture;
    }

    public Date getExpectedTimeSlot() {
        return expectedTimeSlot;
    }

    public void setExpectedTimeSlot(Date expectedTimeSlot) {
        this.expectedTimeSlot = expectedTimeSlot;
    }

    public Date getScheduledTimeSlot() {
        return scheduledTimeSlot;
    }

    public void setScheduledTimeSlot(Date scheduledTimeSlot) {
        this.scheduledTimeSlot = scheduledTimeSlot;
    }

    /* Επιστρέφει το πιό πρόσφατο time slot της πτήσης.
     * Χρησιμοποιείται για 
     *   α) την κατά σύμβαση ταξινόμηση πτήσεων
     *   β) για την ενεργοποίηση χρονικά εξαρτώμενων (ολικώς ή
     *      μερικώς) μεταβάσεων στις καταστάσεις πτήσεων: 
     *          OnTime      --> Boarding
     *          Delayed     --> Boarding 
     *          Boarding    --> Departed
     *   γ) για την εκτέλεση χρονικά εξαρτώμενων (ολικώς ή
     *      μερικώς) δράσεων:
     *          απελευθέρωση αεροδιαδρόμου από Departed πτήση
     *   δ) για το φιλτράρισμα των πρόσφατα Departed και
     *      Cancelled πτήσεων που πρέπει να εμφανίζει ο
     *      ΠΙΝΑΚΑΣ ΑΝΑΧΩΡΗΣΕΩΝ
     */
    public Date getValidTimeSlot() {
        return (this.expectedTimeSlot == null) ? 
                          this.scheduledTimeSlot :
                          this.expectedTimeSlot;
    }
    
    public Airline getAirline() {
        return airline;
    }
            
    // ------------ ΜΕΘΟΔΟΙ ΚΑΤΑΣΚΕΥΑΣΤΕΣ -------------------------------
    
    /* Κατασκευαστής 1 -------
     *  Δημιουργεί μια πτήση με ορίσματα τύπου String και μια
     *  αεροπορική εταιρεία. Αρχικοποιεί με λογικές τιμές τα πεδία
     *  status, scheduledTimeSlot, expectedTimeSlot και μετατρέπει
     *  το scheduledDeparture από String σε Date
     */
    public Flight(String code, String departureTo, String via, 
        String scheduledDeparture, Airline airline) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        this.code = code;
        this.departureTo = departureTo;
        this.via = via;
        setStatus(FlightStatus.OnTime);
        if (scheduledDeparture.isEmpty()) {
            this.initialSchedDeparture = null;
        } else {
            this.initialSchedDeparture = (Date)formatter.parse(scheduledDeparture);
        }
        this.expectedTimeSlot = null;
        this.scheduledTimeSlot = null;
        this.airline = airline;
    }
   
    // ------------------ ΛΕΙΤΟΥΡΓΙΕΣ ------------------------------------
    
    /*  Κάνει σύγκριση πτήσεων με βάση το υπολογιζόμενο πεδίο 
     *  the getValidTimeSlot().
     *  Μπορεί να χρησιμοποιηθεί για την ταξινόμηση πτήσεων
     */
    @Override
    public int compareTo(Flight o) {
        return this.getValidTimeSlot().compareTo(o.getValidTimeSlot());
    }
    
    
    /* Επιστρέφει ένα String που περιγράφει τα στοιχεία της πτήσης
     * Χρησιμοποιείται από τις αντίστοιχες μεθόδους της Airline και
     * του Airport
     */
    @Override
    public String toString() {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String columnsFormat = "%-25s%-15s%-25s%-20s%-20s%-20s%-15s";
        return String.format(columnsFormat, departureTo, code, via,
           ((expectedTimeSlot == null) ? "" : 
                   formatter.format(expectedTimeSlot)),
           ((scheduledTimeSlot == null) ? "" : 
                   formatter.format(scheduledTimeSlot)),
           ((initialSchedDeparture == null) ? "" : 
                   formatter.format(initialSchedDeparture)),
           status.toString());
    }        
}