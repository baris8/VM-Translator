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
        file = f;
        cw = new CodeWriter(file);
        out = "@256\n"
            + "D = A\n"
            + "@SP\n"
            + "M = D\n"
            + cw.writeCall("Sys.init", 0);
    }
    public Parser(File f, boolean t) throws FileNotFoundException{
        file = f;
        cw = new CodeWriter(file);
        out = "";
    }
    
    public void parseVMCode(File asmFile) throws FileNotFoundException{
        Scanner scanner = new Scanner(asmFile);
        while(scanner.hasNext()){
            current = scanner.nextLine();
            if(!current.equals("")){
                current = current.replaceAll("//.*", "").trim();
                switch(cmType(current)){
                    case "C_PUSH":  
                        out += cw.writePushPop("push", arg1(current), arg2(current)); 
                        break;
                    case "C_POP": 
                        out += cw.writePushPop("pop", arg1(current), arg2(current)); 
                        break;
                    case "C_ARITHMETIC": 
                        out += cw.writeArithmetic(current); 
                        break;
                    case "C_LABEL": 
                        out += cw.writeLabel(arg1(current)); 
                        break;
                    case "C_GOTO": 
                        out += cw.writeGoTo(arg1(current)); 
                        break;
                    case "C_IFGOTO": 
                        out += cw.writeIfGoTo(arg1(current)); 
                        break;
                    case "C_CALL": 
                        out += cw.writeCall(arg1(current), arg2(current)); 
                        break;
                    case "C_RETURN": 
                        out += cw.writeReturn(); 
                        break;
                    case "C_FUNCTION": 
                        out += cw.writeFunction(arg1(current), arg2(current)); 
                        break;
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
                case "function": return "C_FUNCTION";
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
            if(command.equals("return")){
                return "C_RETURN";
            }else{
                return "C_ARITHMETIC";
            }
        }
        return "";
    }
    
    //Getter & Setter
    public String getOut(){
        return out;
    }
    public void setFile(File f){
        file = f;
        cw.setFile(f);
    }
}
