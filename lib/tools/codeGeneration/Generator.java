package lib.tools.codeGeneration;

import lib.attributes.Attributes;
import lib.symbolTable.Symbol;
import lib.symbolTable.SymbolArray;
import lib.symbolTable.SymbolTable;

import java.io.FileDescriptor;
import java.io.FileWriter;
import java.io.IOException;

public class Generator {
    public CodeBlock codigo_maquina;
    public SymbolTable tabla_simbolo;
    public Generator(SymbolTable tab_simb) {
        this.codigo_maquina = new CodeBlock();
        this.tabla_simbolo = tab_simb;
    }

    public void apila_valor_simbolo(Symbol simbolo){
        codigo_maquina.addComment("Acceso a variable "+ simbolo.name);
        SRF_del_simbolo(simbolo);

        if(simbolo.parClass == Symbol.ParameterClass.REF){ //las referencias son un puntero a un puntero a valor (los punteros a valor son variables al final)
            codigo_maquina.addInst(PCodeInstruction.OpCode.DRF);
        }
        //si quiero el valor de la dirección, tengo que hacer esto, sino, sería apilar direccion
        codigo_maquina.addInst(PCodeInstruction.OpCode.DRF);

    }

    public void apila_direccion_simbolo(Symbol simbolo){

        SRF_del_simbolo(simbolo);

        if(simbolo.parClass == Symbol.ParameterClass.REF){ //las referencias son un puntero a un puntero a valor (los punteros a valor son variables al final)
            codigo_maquina.addInst(PCodeInstruction.OpCode.DRF);
        }

    }
    //Se supone que el valor a asignar ya esta en la pila --> valor variable --> ASGI
    public void asignacion(Attributes atr_v){
        codigo_maquina.addComment("Asignacion");
        if(atr_v.referencia_simbolo.type != Symbol.Types.ARRAY){
            apila_direccion_simbolo(atr_v.referencia_simbolo);
            codigo_maquina.addInst(PCodeInstruction.OpCode.ASGI);
        }else{
            codigo_maquina.addInst(PCodeInstruction.OpCode.ASG);
        }
    }

    private void SRF_del_simbolo(Symbol simbolo) {
        int nivel_del_simbolo = tabla_simbolo.level - simbolo.nivel;
        codigo_maquina.addInst(PCodeInstruction.OpCode.SRF, nivel_del_simbolo, (int) simbolo.dir);
    }

    public void lista_escribibles(Attributes atr) {
        codigo_maquina.addComment("Escribir expresion");
        //El valor de la expresion esta en la pila
        if (atr.referencia_simbolo == null) { //no es una variable
            if (atr.type == Symbol.Types.INT || atr.type == Symbol.Types.BOOL) { //escribir una constante bool o int, se escribe como números
              //  codigo_maquina.addInst(PCodeInstruction.OpCode.STC, atr.valInt);
                codigo_maquina.addInst(PCodeInstruction.OpCode.WRT, 1);

            } else if (atr.type == Symbol.Types.CHAR) { //escribir una constante char
               // codigo_maquina.addInst(PCodeInstruction.OpCode.STC, atr.valChar);
                codigo_maquina.addInst(PCodeInstruction.OpCode.WRT, 0);
            } else if (atr.type == Symbol.Types.STRING) { //escribir una constante cadena
                escribe_string_por_pantalla(atr.valString);
            }

        }else{
            if(atr.type == Symbol.Types.ARRAY){
                if (((SymbolArray) atr.referencia_simbolo).baseType == Symbol.Types.INT || ((SymbolArray) atr.referencia_simbolo).baseType == Symbol.Types.BOOL) { //escribir una variable bool o int, se escribe como números
                    codigo_maquina.addInst(PCodeInstruction.OpCode.WRT, 1);
                }else if (((SymbolArray) atr.referencia_simbolo).baseType == Symbol.Types.CHAR) { //escribir una variable char
                    codigo_maquina.addInst(PCodeInstruction.OpCode.WRT, 0);
                }

            }else{
               // apila_simbolo(atr);
                if (atr.type == Symbol.Types.INT || atr.type == Symbol.Types.BOOL) { //escribir una variable bool o int, se escribe como números
                    codigo_maquina.addInst(PCodeInstruction.OpCode.WRT, 1);
                }else if (atr.type == Symbol.Types.CHAR) { //escribir una variable char
                    codigo_maquina.addInst(PCodeInstruction.OpCode.WRT, 0);
                }
            }
        }


}
    /*
     *  Se espera un string con el texto deseado y un caracter al principio y al final
     */
    private void escribe_string_por_pantalla(String atr) {
        int unicode_del_caracter;
        codigo_maquina.addComment("escribimos por pantalla: " + atr);
        for (int i = 1; i < atr.length() - 1; i++) {  //empiezo en 1 y acabo en length - 1 porque el match del token incluye las ""
            unicode_del_caracter = String.valueOf(atr.charAt(i)).codePointAt(0);
            codigo_maquina.addInst(PCodeInstruction.OpCode.STC, unicode_del_caracter);
            codigo_maquina.addInst(PCodeInstruction.OpCode.WRT, 0);
        }
    }

    public void apila_valor(Attributes atr){
         if (atr.type == Symbol.Types.INT ){
            codigo_maquina.addInst(PCodeInstruction.OpCode.STC, atr.valInt);
         }else if (atr.type == Symbol.Types.BOOL){
             if(atr.valBool){
                codigo_maquina.addInst(PCodeInstruction.OpCode.STC, 1);
             }else{
                codigo_maquina.addInst(PCodeInstruction.OpCode.STC, 0);
             }
         }else if (atr.type == Symbol.Types.CHAR){
             codigo_maquina.addInst(PCodeInstruction.OpCode.STC, atr.valChar);
         }
    }

    //En la pila esta apilada la posicion a la que se quiere acceder del vector
    public void apila_direccion_vector(Symbol sim_v){
        codigo_maquina.addComment("Acceso a componente de vector");
        String segmentation_fault = CGUtils.newLabel();
        String continuacion_normal = CGUtils.newLabel();

        int maxInd = 0;
        if(sim_v instanceof SymbolArray){
            maxInd = ((SymbolArray) sim_v).maxInd;
        }
        //Comprobacion de indice
        codigo_maquina.addInst(PCodeInstruction.OpCode.DUP);
        codigo_maquina.addInst(PCodeInstruction.OpCode.STC, 0);
        codigo_maquina.addInst(PCodeInstruction.OpCode.GTE);
        codigo_maquina.addInst(PCodeInstruction.OpCode.JMF, segmentation_fault);
        //se ha comprobado que el valor es >= a 0
        codigo_maquina.addInst(PCodeInstruction.OpCode.DUP);
        codigo_maquina.addInst(PCodeInstruction.OpCode.STC, maxInd);
        codigo_maquina.addInst(PCodeInstruction.OpCode.LTE);
        codigo_maquina.addInst(PCodeInstruction.OpCode.JMF, segmentation_fault);
        codigo_maquina.addInst(PCodeInstruction.OpCode.JMP, continuacion_normal);
        //se ha comprobado que el valor es <= a 255
        codigo_maquina.addLabel(segmentation_fault);
        escribe_string_por_pantalla(" El indice usado para el vector " + sim_v.name + " es erroneo ");
        codigo_maquina.addInst(PCodeInstruction.OpCode.LVP);
        codigo_maquina.addLabel(continuacion_normal);
        apila_direccion_simbolo(sim_v);
        codigo_maquina.addInst(PCodeInstruction.OpCode.PLUS); //Se suma la @ inicial del vector y la posicion
    }

    //En la pila esta apilada la posicion a la que se quiere acceder del vector
    public void apila_valor_vector(Symbol sim_v){
        apila_direccion_vector(sim_v);
        codigo_maquina.addInst(PCodeInstruction.OpCode.DRF);
}

    public void apila_valor(int valor){
        codigo_maquina.addInst(PCodeInstruction.OpCode.STC, valor);
    }
    /*
     *  Si el simbolo es un array sólo la dirección, alguien nos tendrá que indexar, de otra maenra, apilamos el valor
     */
    public void apila_simbolo(Attributes atr) {
        if(atr.referencia_simbolo.type != Symbol.Types.ARRAY) {
            apila_valor_simbolo(atr.referencia_simbolo);
        }else{//tendremos que confiar que más adelante nos apilaran el valor con el indice
            apila_direccion_simbolo(atr.referencia_simbolo);
        }
    }

    public void escribo_operador(PCodeInstruction.OpCode operador){
        codigo_maquina.addInst(operador);
    }

    public void put_line() {
        codigo_maquina.addComment("Put_line");
        codigo_maquina.addInst(PCodeInstruction.OpCode.STC, 13);
        codigo_maquina.addInst(PCodeInstruction.OpCode.WRT, 0);
        codigo_maquina.addInst(PCodeInstruction.OpCode.STC, 10);
        codigo_maquina.addInst(PCodeInstruction.OpCode.WRT, 0);
    }

    public void skip_line(){
        codigo_maquina.addComment("Skip_line");
        String bucle_skip_line =  CGUtils.newLabel();
        codigo_maquina.addLabel(bucle_skip_line);
        codigo_maquina.addInst(PCodeInstruction.OpCode.SRF, 0, tabla_simbolo.direcciones_de_los_bloques.get(tabla_simbolo.level)); //SRF 0 LA ULTIMA DIRECCION NO ASIGNADA
        codigo_maquina.addInst(PCodeInstruction.OpCode.RD, 0);
        //hemos leido de entrada estandar y tenemos el valor en la ultima @ no asignada
        codigo_maquina.addInst(PCodeInstruction.OpCode.STC, 10); //\n en codigo ascii
        codigo_maquina.addInst(PCodeInstruction.OpCode.SRF, 0, tabla_simbolo.direcciones_de_los_bloques.get(tabla_simbolo.level));
        codigo_maquina.addInst(PCodeInstruction.OpCode.DRF);
        codigo_maquina.addInst(PCodeInstruction.OpCode.EQ);
        //leemos el valor y lo comparamos con \n
        codigo_maquina.addInst(PCodeInstruction.OpCode.JMF, bucle_skip_line);
        //si no es \n repetimos
    }

    /*
     * Asumimos que tenemos en la pila el valor a transformar
     */
    public void int2char(){
        String etiq_error_int2char =  CGUtils.newLabel();
        String continuacion_normal = CGUtils.newLabel();

        codigo_maquina.addComment("Comprobaciones int2char");
        //duplicamos el valor que tenemos en la función, al final, queremos hacer dos comprobaciones
        //y que el valor sigue en la pila para una posible asignación
        codigo_maquina.addInst(PCodeInstruction.OpCode.DUP);
        codigo_maquina.addInst(PCodeInstruction.OpCode.STC, 0);
        codigo_maquina.addInst(PCodeInstruction.OpCode.GTE);
        codigo_maquina.addInst(PCodeInstruction.OpCode.JMF, etiq_error_int2char);
        //se ha comprobado que el valor es >= a 0
        codigo_maquina.addInst(PCodeInstruction.OpCode.DUP);
        codigo_maquina.addInst(PCodeInstruction.OpCode.STC, 255);
        codigo_maquina.addInst(PCodeInstruction.OpCode.LTE);
        codigo_maquina.addInst(PCodeInstruction.OpCode.JMF, etiq_error_int2char);
        codigo_maquina.addInst(PCodeInstruction.OpCode.JMP, continuacion_normal);
        //se ha comprobado que el valor es <= a 255
        codigo_maquina.addLabel(etiq_error_int2char);
        escribe_string_por_pantalla(" El valor entero en un int2char tiene que ser mayor que 0 y menor de 255 ");
        codigo_maquina.addInst(PCodeInstruction.OpCode.LVP);
        codigo_maquina.addLabel(continuacion_normal);
    }

    public void lista_asignables(Attributes atr){
        codigo_maquina.addComment("Leer");
        if(atr.type != Symbol.Types.ARRAY && atr.referencia_simbolo != null ){
            apila_direccion_simbolo(atr.referencia_simbolo);
            if(atr.type == Symbol.Types.INT){
                codigo_maquina.addInst(PCodeInstruction.OpCode.RD,1);
            }else if(atr.type == Symbol.Types.CHAR){
                codigo_maquina.addInst(PCodeInstruction.OpCode.RD,0);
            }
        }
    }

    public String toString() {
        return codigo_maquina.toString();
    }

    public void fin() throws IOException {
        String nombre = "compilado.pcode";
        FileWriter o = new FileWriter(nombre);
        o.write(toString()); o.close();
        System.out.println("Compilación finalizada. Se ha generado el fichero " + nombre);
    }
}
