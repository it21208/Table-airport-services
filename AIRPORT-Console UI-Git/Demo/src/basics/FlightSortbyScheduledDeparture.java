package basics;

import java.util.Comparator;

// Ταξινόμηση πτήσεων με κριτήριο την αρχική ώρα αναχώρησης της εταιρείας
public class FlightSortbyScheduledDeparture implements Comparator<Flight> {

    @Override
    public int compare(Flight o1, Flight o2) {
        return o1.getInitialSchedDeparture().compareTo(o2.getInitialSchedDeparture()); 
    }     
}
