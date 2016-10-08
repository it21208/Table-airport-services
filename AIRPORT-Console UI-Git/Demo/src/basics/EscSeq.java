package basics;

public interface EscSeq {
    String RedPen = "\033[31m",
           GreenPen = "\033[32m",
           MagentaPen = "\033[35m",
           ReverseColors = "\033[7m",
           BlackPenOnWhite = "\033[30m",
           ClearScreen = "\033[2J\033[H",
           BlinkOn = "\033[5m",
           BlinkOff = "\033[25m",
           Reset = "\033[0m",
           BoldOn = "\033[1m",
           BoldOff = "\033[22m";    
}
