package ArrayListExample;


import java.util.ArrayList;
import java.util.Iterator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author tioa
 */
public class Airline {
    int id;
    String name;
    ArrayList<Flight> flightlist;

    public Airline(int id, String name, ArrayList<Flight> flightlist) {
        this.id = id;
        this.name = name;
        this.flightlist = flightlist;
    }
    
    public String toString() {
        Iterator<Flight> itf = flightlist.iterator();
        Flight f;
        String s = "Airline (" + id + ", " + name + ")\n";
        while (itf.hasNext()) {
            f = itf.next();
            s += ("\t" + f.toString() + "\n");
        }
        return s;
    }
}
