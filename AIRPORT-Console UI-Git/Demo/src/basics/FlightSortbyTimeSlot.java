package basics;

import java.util.Comparator;

// Ταξινόμηση πτήσεων με κριτήριο την αρχική χρονοθυρίδα της πτήσης
public class FlightSortbyTimeSlot implements Comparator<Flight> {

    @Override
    public int compare(Flight o1, Flight o2) {
        return o1.getScheduledTimeSlot().compareTo(o2.getScheduledTimeSlot()); 
    }     
}
