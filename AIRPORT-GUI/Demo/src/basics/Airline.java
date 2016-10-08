package basics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

public class Airline {

    // --------------- ΓΝΩΡΙΣΜΑΤΑ ----------------------------------------
    private final String name;  // ονομασία αεροπορικής εταιρείας
    private final String code;  // κωδικός 2 γραμμάτων αεροπ. εταιρείας
    private final String flightDataFile;  // ονομασία αρχείου πτήσεων
    private final ArrayList<Flight> flightsList;  /* λίστα πτήσεων */
    private Airport airport;  // αεροδρόμιο όπου δώθηκε η λίστα πτήσεων 
   
    // ------------ ΜΕΘΟΔΟΙ ΠΡΟΣΒΑΣΗΣ-ΤΡΟΠΟΠΟΙΗΣΗΣ -----------------------
    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getFlightDataFile() {
        return flightDataFile;
    }

    public ArrayList<Flight> getFlightsList() {
        return flightsList;
    }

    public Airport getAirport() {
        return airport;
    }

    public void setAirport(Airport airport) {
        this.airport = airport;
    }
    
   
    // ------------ ΜΕΘΟΔΟΙ ΚΑΤΑΣΚΕΥΑΣΤΕΣ -------------------------------
    
    /* Κατασκευαστής 1 -------
     *  Δημιουργεί μια αεροπορική εταιρεία με ονομασία, κωδικό και
     *  το αρχείο των πτήσεών της. Από το αρχείο πτήσεων διαβάζονται οι
     *  πληροφορίες των πτήσεων και δημιουργείται η λίστα πτήσεων
     */
    public Airline(String name, String code, String flightDataFile) 
            throws IOException, ParseException {
        this.name = name;
        this.code = code;
        this.flightDataFile = flightDataFile;
        this.flightsList = this.ReadMyFlights();
    }
    
    // ------------------ ΛΕΙΤΟΥΡΓΙΕΣ ------------------------------------
    
    /* Επιστρέφει μια λίστα πτήσεων που τη δημιουργεί διαβάζοντας τις 
     * αντίστοιχες πληροφορίες από το αρχείο πτήσεων της εταιρείας. Καλείται
     * μόνο από τον Κατασκευαστή
     */
    private ArrayList<Flight> ReadMyFlights() throws IOException, ParseException {
        String code, departureTo, via, scheduledDeparture;
        BufferedReader in = null;
        Flight fl;
        Scanner sc = null;
        ArrayList<Flight> arrFlights = new ArrayList<>();
        try {
            in = new BufferedReader(new FileReader(this.flightDataFile));
            String line;
            while ((line = in.readLine()) != null) {
                /* ορίζεται το ", " ως delimiter για να διαβαστούν
                   4 πεδία σε κάθε γραμμή */
                sc = new Scanner(line).useDelimiter(",\\s");
                code = this.code + " " + sc.next();
                departureTo = sc.next();
                via = sc.next();
                scheduledDeparture = sc.next();
                // δημιουργία της πτήσης για αυτήν την αεροπορική εταιρεία
                fl = new Flight(code, departureTo, via, scheduledDeparture, this);
                // προσθήκη της νέας πτήσης στη λίστα πτήσεων
                arrFlights.add(fl);
            }
        } finally { // κλείσιμο των stream που έχουν ανοίξει
            if (in != null) {
                in.close();
            }
            if (sc != null) {
                sc.close();
            }
        }
        return arrFlights;
    }
 
    /* Επιστρέφει ένα String πολλαπλών γραμμών όπου περιγράφονται τα 
     * στοιχεία της αεροπορικής εταιρείας στην 1η γραμμή και ακολούθως
     * σε ξεχωριστή γραμμή και indented τα στοιχεία της κάθε πτήσης της
     * Γενική χρήση πχ. έλεγχοι κατά τη διαδικασία ανάπτυξης
     */
    @Override
    public String toString() {
        Flight f;
        String s = this.name + "\t" + this.code + "\n";
        Iterator<Flight> it = this.flightsList.iterator();
        while (it.hasNext()) {
            s = s + "\t";
            f = it.next();
            s = s + f.toString();
            s = s + "\n";
        }
        return s;
    }
    
    /*
     * Για κάθε πτήση της αεροπορικής εταιρείας ελέγχει και 
     * κάνει τις χρονικά εξαρτώμενες (ολικώς ή μερικώς) μεταβάσεις:
     *          OnTime ||  
     *          Delayed     --> Boarding 
     *          Boarding    --> Departed ||
     *                          Cancelled
     */
    public void refreshStatusOfRemaining() {
        Flight f;
        Iterator<Flight> it = this.flightsList.iterator();
        while (it.hasNext()) {
            f = it.next();
            switch (f.getStatus()) {
                case OnTime: // εάν η πτήση είναι OnTime || Delayed 
                case Delayed:
                    // και ισχύει: (NOW <= ValidTimeSlot < NOW+30min)
                    if (Control.isDateDiffWithCurTimeLessThanMinutes(30,f.getValidTimeSlot()))
                        // η πτήση γίνεται Boarding
                        f.setStatus(FlightStatus.Boarding);
                    break;
                case Boarding: // εάν η πτήση είναι Boarding
                    // και ισχύει: (ValidTimeSlot <= NOW < ValidTimeSlot+1min)
                    if (Control.isCurTimeDiffWithDateLessThanMinutes(1, f.getValidTimeSlot())) {
                        // και υπάρχει ελεύθερος αεροδιάδρομος                 
                        if (this.airport.getNoOfFreeAirways() > 0) {
                            // η πτήση γίνεται Boarding                            
                            f.setStatus(FlightStatus.Departed);
                            // και δεσμεύεται ένας αεροδιάδρομος
                            this.airport.setNoOfFreeAirways(this.airport.getNoOfFreeAirways()- 1);
                        } else { /* αλλά δεν υπάρχει ελεύθερος αεροδιάδρομος
                            τότε η πτήση γίνεται Cancelled */
                            f.setStatus(FlightStatus.Cancelled);
                        }                       
                    }
                    break;
                default :
            }
        }    
    }
    
    /* Ελέγχει τις Departed πτήσεις της αεροπορικής εταιρείας για να
     * διαπιστώσει εάν είναι ώρα να απελευθερωθεί κάποιος διάδρομος
     */
    public void ReleaseAirwaysOfDepartedFlights() {
        Flight f;
        Iterator<Flight> it = this.flightsList.iterator();
        while (it.hasNext()) {
            f = it.next();
            switch (f.getStatus()) {
                case Departed: // εάν η πτήση είναι Departed 
                    /* και ισχύει: 
                       (ValidTimeSlot+1slot <= NOW < ValidTimeSlot+1slot+1min) */
                    if (Control.isCurTimeDiffWithDateLessThanMinutes(1, 
                       new Date(f.getValidTimeSlot().getTime() + 
                           this.airport.getTimeSlotDuration()*60*1000))) {
                        // και υπάρχει κάποιος δεσμευμένος διάδρομος
                        if (this.airport.getNoOfFreeAirways() < this.airport.getMaxAirways()) {
                            // τότε απελευθέρωσε ένα διάδρομο
                            this.airport.setNoOfFreeAirways(this.airport.getNoOfFreeAirways()+ 1);
                        }
                    }
                    break;
                default :
            }
        }    
    }
}
