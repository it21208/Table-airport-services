package basics;

public enum FlightStatus {
    OnTime, 
    Delayed,
    Cancelled,
    Boarding,
    Departed,
    Invalid;
    
    // ------------------ ΛΕΙΤΟΥΡΓΙΕΣ ------------------------------------
    
    /* Επιστρέφει String με το λεκτικό που αντιστοιχεί στο
     * FlightStatus enum
     *
     *  FlightStatus --> String
     */
    @Override
    public String toString() {
        String s = "";
        switch (this) {
         case OnTime:
              s = "OnTime";
              break;
         case Delayed:
              s = "Delayed";
              break;
         case Cancelled:
              s = "Cancelled";
              break;
         case Boarding:
              s = "Boarding";
              break;
         case Departed:
              s = "Departed";
              break;
         case Invalid:
              s = "Invalid";
        }
        return s;
    }
    
    /* Επιστρέφει String με το λεκτικό που αντιστοιχεί στο
     * FlightStatus enum στο οποίο μπροστά έχει προστεθεί
     * το Escape sequence που επιτυγχάνει τα εφέ που ζητήθηκαν
     */    
    public String toColouredString() {
        String s = "";
        switch (this) {
         case OnTime:
              s = EscSeq.GreenPen;
              break;
         case Delayed:
              s = EscSeq.MagentaPen;
              break;
         case Cancelled:
              s = EscSeq.RedPen;
              break;
         case Boarding:
              s = EscSeq.BlinkOn;
              break;
         case Departed:
              s = EscSeq.ReverseColors;
              break;
         default:
        }
        s = s + this.toString();
        return s;
    }
        
    /* Επιστρέφει FlightStatus που αντιστοιχεί στο String
     *
     *  String --> FlightStatus
     */
    public static FlightStatus toFlightStatus(String status) {
        switch (status.toLowerCase()) {
            case "ontime":
                return FlightStatus.OnTime;
            case "delayed":
                return FlightStatus.Delayed;
            case "cancelled":
                return FlightStatus.Cancelled;
            case "boarding":
                return FlightStatus.Boarding;
            case "departed":
                return FlightStatus.Departed;
            default:
                return FlightStatus.Invalid;
        }
    }
}