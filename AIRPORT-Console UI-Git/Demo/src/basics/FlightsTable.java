package basics;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

public class FlightsTable {

    // --------------- ΓΝΩΡΙΣΜΑΤΑ ----------------------------------------
    private final int displayLines; // πλήθος γραμμών πίνακα
    private final int maxRecentlyDepartedFlightsToDisplay;    /* μέγιστο 
            * πλήθος Departed πτήσεων που μπορούν να εμφανίζονται */
    private final int maxRecentlyCancelledFlightsToDisplay;   /* μέγιστο
            * πλήθος Cancelled πτήσεων που μπορούν να εμφανίζονται */
    private final int refreshTime;  // ανανέωση πίνακα κάθε .. min
    private final Airport airport;  // το αεροδρόμιο στο οποίο ανήκει

    // ------------ ΜΕΘΟΔΟΙ ΠΡΟΣΒΑΣΗΣ-ΤΡΟΠΟΠΟΙΗΣΗΣ -----------------------
    public int getDisplayLines() {
        return displayLines;
    }

    public int getMaxRecentlyDepartedFlightsToDisplay() {
        return maxRecentlyDepartedFlightsToDisplay;
    }

    public int getMaxRecentlyCancelledFlightsToDisplay() {
        return maxRecentlyCancelledFlightsToDisplay;
    }

    public int getRefreshTime() {
        return refreshTime;
    }

    public Airport getAirport() {
        return airport;
    }
    
    // ------------ ΜΕΘΟΔΟΙ ΚΑΤΑΣΚΕΥΑΣΤΕΣ -------------------------------
    
    /* Κατασκευαστής 1 -------
     * Δημιουργεί ένα ΠΙΝΑΚΑ ΑΝΑΧΩΡΗΣΕΩΝ για ένα αεροδρόμιο και ορίζει το
     * πλήθος γραμμών που εμφανίζει, το μέγιστο πλήθος Delayed και
     * Cancelled πτήσεων που μπορεί να εμφανίζει και το χρόνο ανανέωσης
     */
    public FlightsTable(int displayLines, int maxRecentlyDepartedFlightsToDisplay, 
            int maxRecentlyCancelledFlightsToDisplay, int refreshTime,
            Airport airport) {
        this.displayLines = displayLines;
        this.maxRecentlyDepartedFlightsToDisplay = maxRecentlyDepartedFlightsToDisplay;
        this.maxRecentlyCancelledFlightsToDisplay = maxRecentlyCancelledFlightsToDisplay;
        this.refreshTime = refreshTime;
        this.airport = airport;
    }

    // ------------------ ΛΕΙΤΟΥΡΓΙΕΣ ------------------------------------

    /*  Εκτελεί μια προσομοίωση του ΠΙΝΑΚΑ ΑΝΑΧΩΡΗΣΕΩΝ για περίοδο κάποιων
     *  λεπτών (min).
     */
    public void runForPeriod(int minutes) throws InterruptedException {
        // υπολογισμός αριθμού επαναλήψεων
        int repetitions = minutes/refreshTime;
        // σε κάθε επανάληψη
        for (int i=1; i<=repetitions; i++) {
            // κάνε ανανέωση της κατάστασης του ΠΙΝΑΚΑ
            this.refreshStatus();
            // περίμενε για διάστημα refreshTime
            Thread.sleep(refreshTime*60*1000);
        }
    }
        
    /* Κάνει ανανέωση του ΠΙΝΑΚΑ ΑΝΑΧΩΡΗΣΕΩΝ.
     * Είναι το κυρίως κομμάτι της προσομοίωσης
     */
    private void refreshStatus() {
        /* 1. καλούνται όλες οι αεροπορικές εταιρείες να ενημερώσουν πόσες
              από τις Departed πτήσεις τους έχουν "πετάξει" και άρα είναι
              δυνατόν να απελευθερωθούν οι αντίστοιχοι διάδρομοι */
        Airline a;
        Iterator<Airline> it = this.airport.getAirlinesList().iterator();
        while (it.hasNext()) {
            a = it.next();
            a.ReleaseAirwaysOfDepartedFlights();
        }
        /* 2. καλούνται όλες οι αεροπορικές εταιρείες να ενημερώσουν την 
              κατάσταση των υπόλοιπων πτήσεών τους */
        it = this.airport.getAirlinesList().iterator();
        while (it.hasNext()) {
            a = it.next();
            a.refreshStatusOfRemaining();
        }
        // 3. δημιουργία της λίστας πτήσεων του ΠΙΝΑΚΑ ΑΝΑΧΩΡΗΣΕΩΝ
        ArrayList<Flight> flightsDisplayList = this.getFlightsListToDisplay();
        Collections.sort(flightsDisplayList, new FlightSortbyTimeSlot());
        // 4. εμφάνιση του ΠΙΝΑΚΑ ΑΝΑΧΩΡΗΣΕΩΝ
        printFlightDisplayList(flightsDisplayList);
    }

     /*  Δημιουργεί τη λίστα πτήσεων του ΠΙΝΑΚΑ ΑΝΑΧΩΡΗΣΕΩΝ
      */
    public ArrayList<Flight> getFlightsListToDisplay() {
        Flight f;
        ArrayList<Flight> flightsList, flightsDisplayList;
        boolean isDeparted, isCancelled;
        boolean hasLeft1Hour, hasLeft6Hours;
        
        int remainingDisplayLines = this.displayLines;
        int maxRecentlyDepartedFlightsToDisplay = 
                this.maxRecentlyDepartedFlightsToDisplay;
        int maxRecentlyCancelledFlightsToDisplay = 
                this.maxRecentlyCancelledFlightsToDisplay;
        /* 1. αντιγραφή της λίστας πτήσεων αεροδρομίου και ταξινόμηση σε
          φθίνουσα σειρά με βάση το κατά σύμβαση κριτήριο getValidTimeSlot() */
        flightsList = (ArrayList<Flight>)this.airport.getFlightsList().clone();
        Collections.sort(flightsList);
        Collections.reverse(flightsList);
        /* 2. δημιουργία νέας λίστας μόνο με τις πτήσεις που ικανοποιούν τις
           συνθήκες:
                    Departed (< 1 hour)     ή 
                    Cancelled (6 < hours)
            Όσες πτήσεις προστίθενται στη νέα λίστα, αφαιρούνται από το
            αντίγραφο της λίστας που δημιουργήθηκε!
        */
        flightsDisplayList = new ArrayList<>();
        Iterator<Flight> it = flightsList.iterator();
        while (it.hasNext()) {
            f = it.next();
            isDeparted = f.getStatus().equals(FlightStatus.Departed);
            isCancelled = f.getStatus().equals(FlightStatus.Cancelled);
            hasLeft1Hour = 
                    Control.isCurTimeDiffWithDateLessThanHours(1, f.getValidTimeSlot());
            hasLeft6Hours = 
                    Control.isCurTimeDiffWithDateLessThanHours(6, f.getValidTimeSlot());
            
            if (isDeparted) { 
                if (hasLeft1Hour && (maxRecentlyDepartedFlightsToDisplay-- > 0)) {
                    flightsDisplayList.add(f);
                    remainingDisplayLines--;
                }
                it.remove();
            }
            if (isCancelled) { 
                if (hasLeft6Hours && (maxRecentlyCancelledFlightsToDisplay-- > 0)) {
                    flightsDisplayList.add(f);
                    remainingDisplayLines--;
                }
                it.remove();
            }
        }
        /* 3. στη νέα λίστα προστίθενται και οι πτήσεις που η χρονοθυρίδα τους
              είναι πιο κοντά στην τρέχουσα χρονική στιγμή αλλά μέχρι το όριο
              των γραμμών του ΠΙΝΑΚΑ ΑΝΑΧΩΡΗΣΕΩΝ */
        Collections.sort(flightsList, new FlightSortbyTimeSlot());
        it = flightsList.iterator();
        while (it.hasNext() && (remainingDisplayLines > 0)) {
            f = it.next();
            flightsDisplayList.add(f);
            remainingDisplayLines--;
        }
        // 4. Επιστρέφει τη νέα λίστα πτήσεων του ΠΙΝΑΚΑ ΑΝΑΧΩΡΗΣΕΩΝ
        return flightsDisplayList;
    }
       
    
    /* Εμφανίζει τη λίστα πτήσεων που δίνεται στον ΠΙΝΑΚΑ ΑΝΑΧΩΡΗΣΕΩΝ
     */
    private void printFlightDisplayList(ArrayList<Flight> flightDisplayList) {
        Flight f;
        int lineNo = 1;
        String columnsFormat = "%-25s%-20s%-10s%-25s%-20s%-20s%-20s%-15s\n";
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Iterator<Flight> it = flightDisplayList.iterator();
        String displayLine = "";
        
        // 1. Καθαρισμός οθόνης και τοποθέτηση άνω-αριστερά
        displayLine += EscSeq.ClearScreen;
        displayLine += "\033[37;45m";
        displayLine += String.format(columnsFormat, 
                "DEPARTURES", "AIRLINE", "FLIGHT", "VIA", 
                "EXPECTED", "SLOT", "SCHEDULED", "REMARKS");
        displayLine += String.format(columnsFormat, 
                "", "", "", "", 
                "DEPARTURE", "", "DEPARTURE", "");
        
        displayLine += EscSeq.Reset; // επαναφορά στο κανονικό
        while (it.hasNext()) {
            f = it.next();
            displayLine += (lineNo++ % 2 == 1) ? "\033[30;46m" : "\033[30;47m";
            displayLine += String.format(columnsFormat, 
                f.getDepartureTo(), f.getAirline().getName(), f.getCode(), 
                (f.getVia().isEmpty() ? "" : f.getVia()), 
                (f.getExpectedTimeSlot() == null ) ? 
                        "" : formatter.format(f.getExpectedTimeSlot()),
                formatter.format(f.getScheduledTimeSlot()),
                formatter.format(f.getInitialSchedDeparture()), 
                f.getStatus().toColouredString());
            displayLine += EscSeq.Reset;
        }
        displayLine += "\033[37;45m";
        displayLine += String.format("Current Time: %1$tH:%1$tM:%1$tS%2$133s", new Date(),"");
        displayLine += EscSeq.Reset;
        System.out.println(displayLine);
//        System.out.println(this.airport.toString());
    }
}
