package lib.tools.codeGeneration;

import lib.attributes.Attributes;
import lib.symbolTable.Symbol;
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

    private void SRF_del_simbolo(Symbol simbolo) {
        int nivel_del_simbolo = tabla_simbolo.level - simbolo.nivel;
        codigo_maquina.addInst(PCodeInstruction.OpCode.SRF, nivel_del_simbolo, (int) simbolo.dir);
    }

    public void lista_escribibles(Attributes atr) {
        codigo_maquina.addComment("Escribir expresion");
        if (atr.referencia_simbolo == null) { //no es una variable
            if (atr.type == Symbol.Types.INT || atr.type == Symbol.Types.BOOL) { //escribir una constante bool o int, se escribe como números

                codigo_maquina.addInst(PCodeInstruction.OpCode.STC, atr.valInt);
                codigo_maquina.addInst(PCodeInstruction.OpCode.WRT, 1);

            } else if (atr.type == Symbol.Types.CHAR) { //escribir una constante char

                codigo_maquina.addInst(PCodeInstruction.OpCode.STC, atr.valChar);
                codigo_maquina.addInst(PCodeInstruction.OpCode.WRT, 0);

            } else if (atr.type == Symbol.Types.STRING) { //escribir una constante cadena
                int unicode_del_caracter;

                for (int i = 1; i < atr.valString.length() - 1; i++) {  //empiezo en 1 y acabo en length - 1 porque el match del token incluye las ""
                    unicode_del_caracter = String.valueOf(atr.valString.charAt(i)).codePointAt(0);
                    codigo_maquina.addInst(PCodeInstruction.OpCode.STC, unicode_del_caracter);
                    codigo_maquina.addInst(PCodeInstruction.OpCode.WRT, 0);
                }

            }

        }else{  //es una variable, se extrae de memoria en memoria
            apila_valor_simbolo(atr.referencia_simbolo);
    }
        put_line();

}

    private void put_line() {
        codigo_maquina.addComment("Put_line");
        codigo_maquina.addInst(PCodeInstruction.OpCode.STC, 13);
        codigo_maquina.addInst(PCodeInstruction.OpCode.WRT, 0);
        codigo_maquina.addInst(PCodeInstruction.OpCode.STC, 10);
        codigo_maquina.addInst(PCodeInstruction.OpCode.WRT, 0);
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