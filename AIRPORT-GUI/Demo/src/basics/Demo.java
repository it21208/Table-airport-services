package basics;

import java.io.IOException;
import java.text.ParseException;

public class Demo {
        
    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
       //AutoExhibit();
       ManualExhibit();
    }
    
    /* Κάνει μια επίδειξη όπως τη ζητάει η εργασία.
     * Πρέπει οι ώρες που αναγράφονται στα αρχεία να είναι σχετικές σε 
     * σχέση με την ώρα που τρέχει το πρόγραμμα.
     */
    private static void ManualExhibit() throws IOException, ParseException, InterruptedException {       
        /* ΑΕΡΟΔΡΟΜΙΟ
           Με ώρα έναρξης 5πμ, διάρκεια λειτουργίας 20 ώρες, διάρκεια
           χρονοθυρίδας 10 λεπτά και 2 διαδρόμους απογείωσης */
        Airport airport = new Airport(5, 20, 10, 2);
        
        /* ΠΙΝΑΚΑΣ ΑΝΑΧΩΡΗΣΕΩΝ ΑΕΡΟΔΡΟΜΙΟΥ
           Με 10 γραμμές πληροφοριών, εμφάνιση το πολύ των 2 πιο 
           πρόσφατων Delayed πτήσεων, εμφάνιση το πολύ των 3 πιο
           πρόσφατων Cancelled πτήσεων και ρυθμό ανανέωσης κάθε
           1 λεπτό */
        airport.InitializeFlightsTable(10, 2, 3, 1);
       
        /* 3 Αεροπορικές Εταιρίες με όνομα, κωδικό και αρχείο πτήσεων
               και
           20 Πτήσεις από αρχεία και από 1 πτήση από το πληκτρολόγιο
           (συνδέονται με κάποια από τις 3 εταιρείες) */
        int i = 0;
        Airline a;
        Flight f;
        while(i++ < 3) {
            a = Control.ReadAirline();
            f = Control.ReadFlight(a);
            airport.loadAirline(a);
        }
                   
        /* Ανάθεση κατάλληλων χρονοθυρίδων στις πτήσεις */
        airport.loadFlightsList();
        
        //Τρέχει ένα πρώτο κύκλο για 1 λεπτό (OnTime --> Boarding)
        airport.getFlightsTable().runForPeriod(1);       
        // Κάνει Delay την πρώτη OnTime πτήση
        Control.DelayTheFirstOnTimeFlight(airport);
        // Τρέχει ένα δεύτερο κύκλο για 60 λεπτά (Boarding --> Departed)
        airport.getFlightsTable().runForPeriod(60);      
    }
    
    /* Κάνει μια αυτοματοποιημένη επίδειξη χωρίς καθόλου εισαγωγή 
     * στοιχείων από το πληκτρολόγιο. Δημιουργεί τα εξής αντικείμενα:
     * 
     *  Α1. 1 Αεροδρόμιο
     *  Α2. 1 Πίνακα Αναχωρήσεων (συνδέεται με το Αεροδρόμιο)
     *  Α3. 3 Αεροπορικές Εταιρίες
     *  Α4. 20 Πτήσεις (συνδέονται με κάποια από τις 3 εταιρείες)
     *  
     * Επίσης κάνει το εξής σενάριο προσομοίωσης:
     *
     *  Β1. Δημιουργεί τυχαίες αρχικές ημερομηνίες αναχώρησης στην περιοχή
     *      των 10 λεπτών έως 50 λεπτών από την ώρα έναρξης του προγράμματος
     *  Β2. Ανάθεση κατάλληλων χρονοθυρίδων στις πτήσεις
     *  Β3. Τρέχει ένα πρώτο κύκλο για 1 λεπτό (OnTime --> Boarding)
     *  Β4. Κάνει Delay την πρώτη OnTime πτήση
     *  Β5. Τρέχει ένα δεύτερο κύκλο για 60 λεπτά (Boarding --> Departed)
     */
    private static void AutoExhibit() throws IOException, ParseException, InterruptedException {       
        /* Α1. ΑΕΡΟΔΡΟΜΙΟ
              Με ώρα έναρξης 5πμ, διάρκεια λειτουργίας 20 ώρες, διάρκεια
              χρονοθυρίδας 10 λεπτά και 2 διαδρόμους απογείωσης */
        Airport airport = new Airport(5, 20, 10, 2);
        
        /* Α2. ΠΙΝΑΚΑΣ ΑΝΑΧΩΡΗΣΕΩΝ ΑΕΡΟΔΡΟΜΙΟΥ
              Με 10 γραμμές πληροφοριών, εμφάνιση το πολύ των 2 πιο 
              πρόσφατων Delayed πτήσεων, εμφάνιση το πολύ των 3 πιο
              πρόσφατων Cancelled πτήσεων και ρυθμό ανανέωσης κάθε
              1 λεπτό */
        airport.InitializeFlightsTable(10, 2, 3, 1);
       
        // Α3. 3 Αεροπορικές Εταιρίες με όνομα, κωδικό και αρχείο πτήσεων
        //      και
        // Α4. 20 Πτήσεις (συνδέονται με κάποια από τις 3 εταιρείες)
        airport.loadAirline(new Airline("Olympic Airlines", "OA", "OAFlights.txt"));
        airport.loadAirline(new Airline("Aegean Airlines", "A3", "A3Flights.txt"));
        airport.loadAirline(new Airline("KLM", "KL", "KLFlights.txt"));
        
        /* Β1. Δημιουργεί τυχαίες αρχικές ημερομηνίες αναχώρησης στην 
               περιοχή των 10 λεπτών έως 50 λεπτών από την ώρα έναρξης
               του προγράμματος */
        Control.RandomizeFlightInitialSchedDeparture(airport, 10, 40);
        
        /* Β2. Ανάθεση κατάλληλων χρονοθυρίδων στις πτήσεις */
        airport.loadFlightsList();
        
        // Β3. Τρέχει ένα πρώτο κύκλο για 1 λεπτό (OnTime --> Boarding)
        airport.getFlightsTable().runForPeriod(1);       
        // Β4. Κάνει Delay την πρώτη OnTime πτήση
        Control.DelayTheFirstOnTimeFlight(airport);
        // Β5. Τρέχει ένα δεύτερο κύκλο για 60 λεπτά (Boarding --> Departed)
        airport.getFlightsTable().runForPeriod(60);      
    }
}
