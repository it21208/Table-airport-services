package basics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

public class Airport {
    
    // --------------- ΓΝΩΡΙΣΜΑΤΑ ----------------------------------------
    private final Date startDateTime;   // έναρξη λειτουργίας αεροδρομίου
    private final int operationDuration; // διάρκεια λειτουργίας (hours)
    private final Date endDateTime;     // λήξη λειτουργίας αεροδρομίου
    private final int timeSlotDuration; // διάρκεια σε (min) χρονοθυρίδας
    private final int maxAirways;   // εγκατεστημένοι διάδρομοι 
    private int noOfFreeAirways;    // ελεύθεροι διάδρομοι
    private FlightsTable flightsTable;  // ΠΙΝΑΚΑΣ ΑΝΑΧΩΡΗΣΕΩΝ αεροδρομίου
    private ArrayList<Airline> airlinesList; // λίστα αεροπορικών εταιρειών
    private ArrayList<Flight> flightsList; /* λίστα καταχωρημένων πτήσεων
           * που τους έχει ανατεθεί το κοντινότερο time slot (χρονοθυρίδα) */
    private byte[] timeSlot; /* πίνακας με την κατάσταση κάθε time slot,
            * δηλαδή τον αριθμό των πτήσεων που έχουν ανατεθεί στην κάθε
            * χρονοθυρίδα */

    // ------------ ΜΕΘΟΔΟΙ ΠΡΟΣΒΑΣΗΣ-ΤΡΟΠΟΠΟΙΗΣΗΣ -----------------------
    public Date getStartDateTime() {
        return startDateTime;
    }

    public int getOperationDuration() {
        return operationDuration;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public int getTimeSlotDuration() {
        return timeSlotDuration;
    }

    public int getMaxAirways() {
        return maxAirways;
    }

    public int getNoOfFreeAirways() {
        return noOfFreeAirways;
    }

    /* Γίνεται στοιχειώδης έλεγχος της τιμής έτσι ώστε:
     *   0 <= noOfFreeAirways <= maxAirways
     */
    public void setNoOfFreeAirways(int noOfFreeAirways) {
        if ((noOfFreeAirways >= 0) && (noOfFreeAirways <= maxAirways))
            this.noOfFreeAirways = noOfFreeAirways;
        else
            System.err.format("Λάθος: Airport.setNoOfAirways(<εκτός ορίων>%d)",
                    noOfFreeAirways);
    }

    public FlightsTable getFlightsTable() {
        return flightsTable;
    }

    public ArrayList<Airline> getAirlinesList() {
        return airlinesList;
    }

    public void setAirlinesList(ArrayList<Airline> airlinesList) {
        this.airlinesList = airlinesList;
    }

    public ArrayList<Flight> getFlightsList() {
        return flightsList;
    }

    public byte[] getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(byte[] timeSlot) {
        this.timeSlot = timeSlot;
    }
    
    
    // ------------ ΜΕΘΟΔΟΙ ΚΑΤΑΣΚΕΥΑΣΤΕΣ -------------------------------
    
    /* Κατασκευαστής 1 -------
     *  Δημιουργεί ένα αεροδρόμιο με συγκεριμένη ώρα έναρξης, διάρκεια
     *  λειτουργίας (hours), διάρκεια χρονοθυρίδας (min), αριθμό
     *  διαδρόμων.
     *  Υπολογίζονται αυτόματα η ώρα λήξης λειτουργίας και το πλήθος
     *  των χρονοθυρίδων.
     */
    public Airport(int startTime, int operationDuration, 
            int timeSlotDuration, int maxAirways) {
        Calendar calendar;
        /* με βάση το startTime π.χ 5 δημιουργείται ένα calendar που δείχνει
           στις 05:00πμ σήμερα και αρχικοποιείται η ώρα έναρξης αεροδρομίου
        */
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, startTime);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        this.startDateTime = calendar.getTime();
        this.operationDuration = operationDuration;
        // στην ώρα έναρξης αεροδρομίου προστίθεται η διάρκεια λειτουργίας
        // και αρχικοποιείται η ώρα λήξης αεροδρομίου
        calendar.add(Calendar.HOUR_OF_DAY, operationDuration);
        this.endDateTime = calendar.getTime();
        this.timeSlotDuration = timeSlotDuration;
        this.maxAirways = maxAirways;
        this.noOfFreeAirways = maxAirways;
        this.flightsTable = null;
        this.airlinesList = new ArrayList<>();
        this.flightsList = new ArrayList<>();
        // από τη διάρκεια λειτουργίας υπολογίζεται το πλήθος των time slot
        int noOfTimeSlots = (int)((operationDuration*60) / timeSlotDuration);
        // δημιουργία του πίνακα χρονοθυρίδων
        this.timeSlot = new byte[noOfTimeSlots];
    }
    
    // ------------------ ΛΕΙΤΟΥΡΓΙΕΣ ------------------------------------
    
    // Δημιουργεί και αρχικοποιεί τον ΠΙΝΑΚΑ ΑΝΑΧΩΡΗΣΕΩΝ του αεροδρομίου
    public void InitializeFlightsTable(int displayLines, 
            int maxRecentlyDepartedFlightsToDisplay, 
            int maxRecentlyCancelledFlightsToDisplay, int refreshTime) {
        this.flightsTable = 
            new FlightsTable(displayLines, maxRecentlyDepartedFlightsToDisplay,
                  maxRecentlyCancelledFlightsToDisplay, refreshTime, this);
    }
    
    /*  Αναθέτει μια αεροπορική εταιρεία στο αεροδρόμιο.
     *  Προσομοιώνει δηκαδή την ενέργεια ότι κάθε πρωί οι αεροπορικές
     *  δίνουν τα δεδομένα πτήσεων τους στο αεροδρόμιο.
     */
    public void loadAirline(Airline airline) {
        this.airlinesList.add(airline);
        airline.setAirport(this);
    }
    
    /*  Όταν επιστρέφει true έχει φτιάξει επιτυχώς την πλήρη λίστα
     *  πτήσεων όλων των αεροπορικών εταιριών και μάλιστα έχει
     *  αναθέσει το κατάλληλο time slot με βάση την προγρ. ώρα από 
     *  την αεροπορική εταιρεία, το πλήθος των εγκατεστημένων
     *  διαδρόμων.
     *  Η λίστα πτήσεων ταξινομείται με αύξουσα σειρά με βάση τη
     *  χρονοθυρίδα και γίνεται η λίστα πτήσεων του αεροδρομίου
     */
    public boolean loadFlightsList() {
        Airline a;
        Flight f;
        ArrayList<Flight> arrFlights;      
        Iterator<Flight> itf;
        Iterator<Airline> ita;

        // Δημιουργία πλήρους (αταξινόμητης) λίστας πτήσεων
        arrFlights = new ArrayList<>();
        ita = this.airlinesList.iterator();
        while (ita.hasNext()) {
            a = ita.next();
            arrFlights.addAll((Collection)a.getFlightsList());
        }
        /* εάν η προγρ. ώρα από την αεροπορική εταιρεία είναι εκτός
           ωραρίου λειτουργίας του αεροδρομίου,
           τότε κάνε έξοδο νωρίς και επέστρεψε false */
        if (!areFlightsListScheduledDeparturesValid(arrFlights)) {
            System.err.println("Λάθος: Airport.loadFlightsList() -"
                 + " Αποτυχία ελέγχου ορθότητας λίστας πτήσεων");
            return false;
        }
        
        /* Η λίστα πτήσεων ταξινομείται με αύξουσα σειρά με βάση το
           scheduledDeparture (η προγρ. ώρα από την αεροπορική εταιρεία) */
        Collections.sort(arrFlights, new FlightSortbyScheduledDeparture());
        // ανάθεση χρονοθυρίδας σε κάθε πτήση
        itf = arrFlights.iterator();
        while (itf.hasNext()) {
            f = itf.next();
            if (!this.getSlotForOnTimeFlight(f)) {
                System.err.println("Λάθος: Airport.loadFlightsList() -"
                + " Αποτυχία ανάθεσης χρονοθυρίδων σε όλη τη λίστα πτήσεων");
                return false;
            }
        }
        /* Η λίστα πτήσεων ταξινομείται με αύξουσα σειρά με βάση το
           time slot (χρονοθυρίδα) που μόλις τους ανατέθηκε */
        Collections.sort(arrFlights, new FlightSortbyTimeSlot());
        // αυτή η λίστα πτήσεων γίνεται η λίστα πτήσεων του αεροδρομίου
        this.flightsList = arrFlights;
        // επιτυχής τερματισμός
        return true;
    }
 
    /* Επιστρέφει true αν για κάθε πτήση της λίστας η προγρ. ώρα από 
     * την αεροπορική εταιρεία είναι εντός ωραρίου λειτουργίας του 
     * αεροδρομίου
     */
    private boolean areFlightsListScheduledDeparturesValid(
            ArrayList<Flight> flightsList) {
        boolean validScheduledDeparture;
        Flight f;
        Iterator<Flight> itf = flightsList.iterator();
        while (itf.hasNext()) {
            f = itf.next();
            validScheduledDeparture =
                this.startDateTime.before(f.getInitialSchedDeparture()) &&
                this.endDateTime.after(f.getInitialSchedDeparture());
            if (!validScheduledDeparture) {
                System.err.println("Λάθος: "
                + " Flight.initialSchedDeparture εκτός ωραρίου λειτουργίας");
                return false;
            }
        }
        return true;
    }
    
    // Ανάθεση χρονοθυρίδας σε μια πτήση OnTime για πρώτη φορά
    public boolean getSlotForOnTimeFlight(Flight f) {
        long msecScheduledDeparture, msecStartDateTime, 
             msecTimeSlotDuration, msecIndexTimeSlot, msecNow;
        int slotIndex;
        boolean slotIndexFound;
             
        /* εάν η πτήση δεν είναι OnTime ή έχει ανατεθεί σε κάποια
           χρονοθυρίδα τότε έξοδος */
        if ((f.getStatus() != FlightStatus.OnTime) ||
            (f.getScheduledTimeSlot() != null))   
            return false;
        
        // μετατροπή σε msec του Airport.startDateTime
        msecStartDateTime = this.startDateTime.getTime();
        // μετατροπή σε msec του Airport.timeSlotDuration
        msecTimeSlotDuration = this.timeSlotDuration*60*1000;
        // μετατροπή σε msec του Flight.getScheduledDeparture
        msecScheduledDeparture = f.getInitialSchedDeparture().getTime();
        /* η πιο κατάλληλη χρονοθυρίδα είναι αυτή που είναι ίση ή
           μικρότερη του initialSchedDeparture */
        slotIndex = (int)((msecScheduledDeparture - msecStartDateTime) /
                    msecTimeSlotDuration);
        // αν αυτή η χρονοθυρίδα είναι διαθέσιμη
        if (this.timeSlot[slotIndex] < this.maxAirways) {
            // δέσμευσέ την
            this.timeSlot[slotIndex]++;
        } else { // αλλιώς βρες την επόμενη διαθέσιμη μετά από αυτήν
            slotIndex++;
            do {
                slotIndexFound = 
                        (this.timeSlot[slotIndex++] < this.maxAirways);
            } while ((!slotIndexFound) && (slotIndex < this.timeSlot.length));
            if (!slotIndexFound) {
                System.err.println("Λάθος: Airport.getSlotForOnTimeFlight() -"
             + " Δεν υπάρχει διαθέσιμη χρονοθυρίδα για την πτήση!");
                return false;
            } 
            this.timeSlot[--slotIndex]++;
        }
        // υπολογισμός ώρας έναρξης χρονοθυρίδας σε msec
        msecIndexTimeSlot = msecStartDateTime + 
                            slotIndex*msecTimeSlotDuration;
        // αποκοπή των sec και msec
        msecIndexTimeSlot = msecIndexTimeSlot - (msecIndexTimeSlot % 60000);
        // ανάθεση χρονοθυρίδας σε πτήση
        f.setScheduledTimeSlot(new Date(msecIndexTimeSlot));
        // επιτυχής τερματισμός        
        return true;
    }
    
    /* Ανάθεση χρονοθυρίδας σε μια πτήση Delayed
     * (μπορεί να γίνει πολλές φορές)
     */
    public boolean getSlotForDelayedFlight(Flight f) {
        long msecStartDateTime, msecTimeSlotDuration, 
                msecIndexTimeSlot, msecTimeSlot;
        int slotIndex;
        boolean slotIndexFound;
             
        // εάν η πτήση δεν είναι Delayed τότε έξοδος
        if (f.getStatus() != FlightStatus.Delayed)
            return false;
  
        // μετατροπή σε msec του Airport.startDateTime
        msecStartDateTime = this.startDateTime.getTime();
        // μετατροπή σε msec του Airport.timeSlotDuration
        msecTimeSlotDuration = this.timeSlotDuration*60*1000;
        // μετατροπή σε msec του Flight.getValidTimeSlot
        msecTimeSlot = f.getValidTimeSlot().getTime();
        // βρες την επόμενη διαθέσιμη χρονοθυρίδα
        slotIndex = (int)((msecTimeSlot - msecStartDateTime) /
                           msecTimeSlotDuration) + 1;
        do {
            slotIndexFound = 
                    (this.timeSlot[slotIndex++] < this.maxAirways);

        } while ((!slotIndexFound) && (slotIndex < this.timeSlot.length));
        if (!slotIndexFound) {
            System.err.println("Λάθος: Airport.getSlotForDelayedFlight() -"
         + " Δεν υπάρχει διαθέσιμη χρονοθυρίδα για την πτήση!");
            return false;
        } 
        this.timeSlot[--slotIndex]++;
        // υπολογισμός ώρας έναρξης χρονοθυρίδας σε msec
        msecIndexTimeSlot = msecStartDateTime + 
                            slotIndex*msecTimeSlotDuration;
        // αποκοπή των sec και msec
        msecIndexTimeSlot = msecIndexTimeSlot - (msecIndexTimeSlot % 60000);
        // ανάθεση χρονοθυρίδας σε πτήση
        f.setExpectedTimeSlot(new Date(msecIndexTimeSlot));
        // επιτυχής τερματισμός         
        return true;
    }
    
    /* Επιστρέφει ένα String πολλαπλών γραμμών όπου περιγράφονται τα 
     * στοιχεία όλων των πτήσεων του αεροδρομίου.
     * Γενική χρήση πχ. έλεγχοι κατά τη διαδικασία ανάπτυξης
     */    
    public String toString() {
        String s = "ΑΕΡΟΔΡΟΜΙΟ\n";
        Flight f;
        Iterator<Flight> it = this.flightsList.iterator();
        while (it.hasNext()) {
            f = it.next();
            s += (f.toString() + "\n");
        }
        return s;
    }    
}
