/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ArrayListExample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 *
 * @author tioa
 */
public class Demo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // create 3 Flights in an ArrayList<Flight>
        ArrayList<Flight> flightList = new ArrayList<>();
        flightList.add(new Flight(1, "Flight 1"));
        flightList.add(new Flight(2, "Flight 2"));
        flightList.add(new Flight(3, "Flight 3"));
        Airline airline = new Airline(1, "Airline 1", flightList);
        System.out.print(airline.toString());
        
        
    // Case1   ArrayList<Flight> fl = (ArrayList<Flight>)airline.flightlist.clone();
    // Case2   ArrayList<Flight> fl = (ArrayList<Flight>)airline.flightlist;
        Collections.reverse(fl);
        Iterator<Flight> itf = fl.iterator();
        itf.next();
        itf.next();
        itf.remove();
        System.out.print(airline.toString());
        
        airline.flightlist = fl;
        System.out.print(airline.toString());
    }
    
}
