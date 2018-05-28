package vmtranslatorgui;

import java.io.FileNotFoundException;

public class CodeWriter {
    private int count;
    
    //HilfsStrings für Übersicht
    private final String addSubAndOr;
    private final String eqGtLt;
    private final String pushC;
    private final String popC;
    String functionName;
    
    public CodeWriter() throws FileNotFoundException{
        count = 0;
        addSubAndOr = "@SP\n"
                    + "M = M-1\n"
                    + "A = M\n"
                    + "D = M\n"
                    + "@SP\n"
                    + "M = M-1\n"
                    + "A = M\n";
        eqGtLt = "@SP\n"
               + "M = M-1\n"
               + "A = M\n"
               + "D = M\n"
               + "A = A-1\n"
               + "D = M-D\n";
        pushC = "@SP\n"
                + "A = M\n"
                + "M = D\n"
                + "@SP\n"
                + "M = M+1\n";
        popC = "@R13\n"
                + "M = D\n"
                + "@SP\n"
                + "M = M-1\n"
                + "A = M\n"
                + "D = M\n"
                + "@R13\n"
                + "A = M\n"
                + "M = D\n";
        
    }
    
    public String writeArithmetic(String command){
        String out = "";
        switch(command){
            case "add": out = addSubAndOr + "M = M+D\n@SP\nM = M+1\n"; break;
            case "sub": out = addSubAndOr + "M = M-D\n@SP\nM = M+1\n"; break;
            case "and": out = addSubAndOr + "M = M&D\n@SP\nM = M+1\n"; break;
            case "or":  out = addSubAndOr + "M = M|D\n@SP\nM = M+1\n"; break;
            case "neg": out = "@SP\n"
                    + "M = M-1\n"
                    + "A = M\n"
                    + "M = -M\n"
                    + "@SP\n"
                    + "M = M+1\n"; break;
            case "not": out = "@SP\n"
                    + "M = M-1\n"
                    + "A = M\n"
                    + "M = !M\n"
                    + "@SP\n"
                    + "M = M+1\n"; break;
            case "eq": out = eqGtLt 
                    + "@JEQ_TRUE_"+ count +"\n"
                    + "D;JEQ\n"
                    + "@SP\n"
                    + "A = M-1\n"
                    + "M = 0\n"
                    + "@JEQ_FALSE_" + count + "\n"
                    + "0;JMP\n"
                    + "(JEQ_TRUE_"+count+ ")\n"
                    + "@SP\n"
                    + "A = M-1\n"
                    + "M = -1\n"
                    + "(JEQ_FALSE_" +count+")\n";
                    count++; break;
            case "gt": out = eqGtLt 
                    + "@JGT_TRUE_"+ count +"\n"
                    + "D;JGT\n"
                    + "@SP\n"
                    + "A = M-1\n"
                    + "M = 0\n"
                    + "@JGT_FALSE_" + count + "\n"
                    + "0;JMP\n"
                    + "(JGT_TRUE_"+count+ ")\n"
                    + "@SP\n"
                    + "A = M-1\n"
                    + "M = -1\n"
                    + "(JGT_FALSE_" +count+")\n";
                    count++; break;
            case "lt": out = eqGtLt 
                    + "@JLT_TRUE_"+ count +"\n"
                    + "D;JLT\n"
                    + "@SP\n"
                    + "A = M-1\n"
                    + "M = 0\n"
                    + "@JLT_FALSE_" + count + "\n"
                    + "0;JMP\n"
                    + "(JLT_TRUE_"+count+ ")\n"
                    + "@SP\n"
                    + "A = M-1\n"
                    + "M = -1\n"
                    + "(JLT_FALSE_" +count+")\n";
                    count++; break;
        }
        return out;
    }
    public String writePushPop(String command, String segment, int index){
        String out = "";
        
        if(command.equals("push")){
            switch(segment){
                case "constant": out = "@" + index + "\n"
                        + "D = A\n" 
                        + pushC; 
                    break;
                case "local": out = "@LCL\n"
                        + "D = M\n"
                        + "@"+ index + "\n"
                        + "A = A+D\n"
                        + "D = M\n"
                        + pushC; 
                    break;
                case "argument": out = "@ARG\n"
                        + "D = M\n"
                        + "@"+ index + "\n"
                        + "A = A+D\n"
                        + "D = M\n"
                        + pushC; 
                    break;
                case "this": out = "@THIS\n"
                        + "D = M\n"
                        + "@"+ index + "\n"
                        + "A = A+D\n"
                        + "D = M\n"
                        + pushC; 
                    break;
                case "that": out = "@THAT\n"
                        + "D = M\n"
                        + "@"+ index + "\n"
                        + "A = A+D\n"
                        + "D = M\n"
                        + pushC; 
                    break;
                case "temp": out = "@R5\n" 
                        + "D = A\n" 
                        + "@" + index + "\n" 
                        + "A = D+A\n" 
                        + "D = M\n"
                        + pushC; 
                    break;                
                case "pointer":
                    if(index == 0){
                        out += "@THIS\n"
                            + "D = M\n"
                            + pushC;
                    }
                    if(index == 1){
                        out += "@THAT\n"
                            + "D = M\n"
                            + pushC;
                    }
                    break;
                case "static": out += "@16\n" 
                                    + "D = A\n"
                                    + "@" + index + "\n" 
                                    + "A = D+A\n" 
                                    + "D = M\n"
                                    + "@SP\n"
                                    + "A = M\n"
                                    + "M = D\n"
                                    + "@SP\n"
                                    + "M = M+1\n";
                    break;
            }
        }
        if(command.equals("pop")){
            switch(segment){
                case "local": out = "@LCL\n"
                        + "D = M\n"
                        + "@"+ index + "\n"
                        + "D = D+A\n"
                        + popC;
                    break;
                case "argument": out = "@ARG\n"
                        + "D = M\n"
                        + "@"+ index + "\n"
                        + "D = D+A\n"
                        + popC;
                    break;
                case "this": out = "@THIS\n"
                        + "D = M\n"
                        + "@"+ index + "\n"
                        + "D = D+A\n"
                        + popC;
                    break;
                case "that": out = "@THAT\n"
                        + "D = M\n"
                        + "@"+ index + "\n"
                        + "D = D+A\n"
                        + popC;
                    break;
                case "temp": out = "@R5\n"
                        + "D = A\n"
                        + "@" + index + "\n"
                        + "D = D+A\n"
                        + popC;
                    break;  
                case "pointer":
                    if(index == 0){
                        out += "@THIS\n"
                            + "D = A\n"
                            + popC;
                    }
                    if(index == 1){
                        out += "@THAT\n"
                            + "D = A\n"
                            + popC;
                    }
                    break;
                case "static": out += "@16\n"
                                    + "D = A\n"
                                    + "@" + index + "\n"
                                    + "D = D+A\n"
                                    + popC;
            }
        }
        return out;
    }
    public String writeLabel(String label){
        return "(" + functionName + "$" + label + ")\n";
    }
    public String writeGoTo(String label){
        String out = "@" + functionName + "$" + label + "\n"
                    + "0;JMP\n";
        return out;
    }
    public String writeIfGoTo(String label){
        String out = "@SP\n"
                    + "M = M-1\n"
                    + "A = M\n"
                    + "D = M\n"
                    + "@" + functionName + "$" + label + "\n"
                    + "D;JNE\n";
        return out;
    }
    public String writeCall(String fName, int numArgs){
        return "";
    }
    public String writeFunction(String fName, int numLocals){
        return "";
    }
    public String writeReturn(){
        return "";
    }
} 