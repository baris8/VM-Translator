package vmtranslatorgui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Parser {
    private File file;
    private CodeWriter cw;
    private String current;
    private String out;
    
    public Parser(File f) throws FileNotFoundException{
        out = "";
        cw = new CodeWriter();
        file = f;
        parseVMCode();
    }
    
    public void parseVMCode() throws FileNotFoundException{
        Scanner scanner = new Scanner(file);
        while(scanner.hasNext()){
            current = scanner.nextLine();
            if(current.contains("//")){
                current = current.split("//")[0];
            }
            if(!current.equals("")){
                current = current.replaceAll("//.*", "").trim();
                System.out.println(current);
                switch(cmType(current)){
                    case "C_PUSH":  out += cw.writePushPop("push", arg1(current), arg2(current)); break;
                    case "C_POP": out += cw.writePushPop("pop", arg1(current), arg2(current)); break;
                    case "C_ARITHMETIC": out += cw.writeArithmetic(current); break;
                    case "C_LABEL": out += cw.writeLabel(arg1(current)); break;
                    case "C_GOTO": out += cw.writeGoTo(arg1(current)); break;
                    case "C_IFGOTO": out += cw.writeIfGoTo(arg1(current)); break;
                    case "C_CALL": out += cw.writeCall(); break;
                    case "C_RETURN": break;
                    case "C_FUNCTION": break;
                }
            }
        }
    }
    public String arg1(String in){
        return in.split(" ")[1];
    }
    public int arg2(String in){
        return Integer.parseInt(in.split(" ")[2]);
    }
    
    public String cmType(String command){
        if(command.split(" ").length == 3){
            switch(command.split(" ")[0]){
                case "push": return "C_PUSH";
                case "pop": return "C_POP";
                case "call": return "C_CALL";
            }
        }
        if(command.split(" ").length == 2){
            switch(command.split(" ")[0]){
                case "label": return "C_LABEL";
                case "goto": return "C_GOTO";
                case "if-goto": return "C_IFGOTO";
            }
        }
        if(command.split(" ").length == 1){
            return "C_ARITHMETIC";
        }
        return "";
    }
    
    public String getOut(){
        return out;
    }
}
