package ArrayListExample;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author tioa
 */
public class Flight implements Comparable<Flight> {
    int id;
    String name;

    public Flight(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String toString() {
        return "Flight (" + id + ", " + name + ")";
    }

    @Override
    public int compareTo(Flight o) {
        return (id == o.id) ? 0 : ((id < o.id) ? -1 : 1);
    }
    

}
