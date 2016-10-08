package basics;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

public abstract class Control {
    
    // ------------------ ΛΕΙΤΟΥΡΓΙΕΣ ------------------------------------
    
    /* Δημιουργεί μια πτήση για μια αεροπορική εταιρεία ζητώντας
     * τα στοιχεία από το πληκτρολόγιο
     */
    public static Flight ReadFlight(Airline airline) throws ParseException{
        Flight f;
        Scanner keyboard = new Scanner(System.in);
        System.out.println("ΕΙΣΑΓΩΓΗ ΠΤΗΣΗΣ ΓΙΑ ΤΗΝ " + airline.getName());
        System.out.print("Κωδικός : ");
        String code = keyboard.nextLine();
        System.out.print("Προορισμός : ");
        String departureTo = keyboard.nextLine();
        System.out.print("Ενδιάμεσος : ");
        String via = keyboard.nextLine();
        System.out.print("Προγραμματισμένη αναχώρηση : ");
        String scheduledDeparture = keyboard.nextLine();
        f = new Flight(code, departureTo, via, scheduledDeparture, airline);
        airline.getFlightsList().add(f);
        return f;
    }
    
    /* Δημιουργεί μια αεροπορική εταιρεία ζητώντας
     * τα στοιχεία από το πληκτρολόγιο
     */
    public static Airline ReadAirline() throws IOException, ParseException{
        Scanner keyboard = new Scanner(System.in);
        System.out.println("ΕΙΣΑΓΩΓΗ ΑΕΡΟΠΟΡΙΚΗΣ ΕΤΑΙΡΕΙΑΣ");
        System.out.print("'Ονομα : ");
        String name = keyboard.nextLine();
        System.out.print("Κωδικός : ");
        String code = keyboard.nextLine();
        System.out.print("Όνομα αρχείου πτήσεων : ");
        String flightDataFile = keyboard.nextLine();
        return new Airline(name,code, flightDataFile); 
    }
    
    /* Επιστρέφει true εάν:
     *   dt <= NOW < dt+minutes
     */
    public static boolean isCurTimeDiffWithDateLessThanMinutes
        (long minutes, Date dt) {
        // βρίσκει τα msec της τρέχουσας ημέρας και ώρας
        long msecsNow = new Date().getTime();
        // βρίσκει τα msec του dt
        long msecsdt = dt.getTime();
        // Αν δεν ικανοποιείται η αριστερή ανισότητα dt <= now
        // επιστροφή με false
        if (msecsNow < msecsdt)
            return false;
        // βρίσκει τη διαφορά (NOW - dt) σε min
        long minDiff = (msecsNow - msecsdt)/(60*1000);
        // επιστρέφει το αποτέλεσμα της σύγκρισης
        return minDiff < minutes;
    }

    /* Επιστρέφει true εάν:
     *   dt <= NOW < dt+hours
     */
    public static boolean isCurTimeDiffWithDateLessThanHours
        (int hours, Date dt) {
        return isCurTimeDiffWithDateLessThanMinutes(hours*60, dt);
    }

    /* Επιστρέφει true εάν:
     *   NOW <= dt < NOW+minutes
     */
    public static boolean isDateDiffWithCurTimeLessThanMinutes
        (long minutes, Date dt) {
       // βρίσκει τα msec της τρέχουσας ημέρας και ώρας
        long msecsNow = new Date().getTime();
        // βρίσκει τα msec του dt
        long msecsdt = dt.getTime();
        // Αν δεν ικανοποιείται η αριστερή ανισότητα NOW <= dt
        // επιστροφή με false
        if (msecsdt < msecsNow)
            return false;
        // βρίσκει τη διαφορά (NOW - dt) σε min
        long minDiff = (msecsdt - msecsNow)/(60*1000);
        // check if minDiff is less than <minutes> parameter and return 
        // the result
        return minDiff < minutes;
    }
    
    /* Δημιουργεί τυχαία initialSchedDeparture για όλες τις πτήσεις που
     * έχουν ανατεθεί στο αεροδρόμιο. Οι χρόνοι που επιστρέφονται είναι
     * στρογγυλοποιηθεί σε min και είναι στην περιοχή:
     *  [Now + startmin]..[Now + startmin + duration]
     *
     * - Χρησιμοποιήθηκε κατά τη διάρκεια των τέστ έτσι ώστε να μη
     * χρειάζεται πληκτρολόγηση νέων ημερομηνιών στις πτήσεις.
     * - Πρέπει να τρέξει πριν την airport.loadFlightsList() που
     * κάνει την αρχική ανάθεση των time slot
     */
    public static void RandomizeFlightInitialSchedDeparture(Airport airport, 
            int startMin, int duration) {
        Airline a;
        Flight f;
        Iterator<Airline> ita;
        Iterator<Flight> itf;
        long rndMillis;
        ita = airport.getAirlinesList().iterator();
        while (ita.hasNext()) {
            a = ita.next();
            itf = a.getFlightsList().iterator();
            while (itf.hasNext()) {
                f = itf.next();
                rndMillis = new Date().getTime() + (startMin*60*1000) +
                            (long)(Math.random()*(duration*60*1000));
                // μηδενίζουμε τα sec και τα msecs
                rndMillis = rndMillis - (rndMillis % 60000);
                f.setInitialSchedDeparture(new Date(rndMillis));
            }
        }
    }
    
    /* Κάνει την πρώτη OnTime πτήση Delayed προκειμένου να 
     * προσομοιάσει κάποιο πραγματικό γεγονός */
    public static void DelayTheFirstOnTimeFlight(Airport airport) {
        Flight f;
        Iterator<Flight> itf = airport.getFlightsList().iterator();
        while (itf.hasNext()) {
            f = itf.next();
            if (f.getStatus() == FlightStatus.OnTime) {
                f.setStatus(FlightStatus.Delayed);
                return;
            }
        }
    }
}