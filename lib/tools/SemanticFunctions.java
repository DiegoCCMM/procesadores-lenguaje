//*****************************************************************
// Tratamiento de errores sintácticos
//
// Fichero:    SemanticFunctions.java
// Fecha:      03/03/2022
// Versión:    v1.0
// Asignatura: Procesadores de Lenguajes, curso 2021-2022
//*****************************************************************

package lib.tools;

import lib.errores.ErrorSemantico;
import lib.symbolTable.Symbol;
import lib.symbolTable.SymbolArray;
import lib.symbolTable.SymbolBool;
import lib.symbolTable.SymbolChar;
import lib.symbolTable.SymbolInt;
import lib.symbolTable.SymbolTable;
import lib.symbolTable.exceptions.AlreadyDefinedSymbolException;
import traductor.*;

public class SemanticFunctions {
    private ErrorSemantico errSem; //clase comun de errores semanticos

    public SemanticFunctions() {
        errSem = new ErrorSemantico();
    }

    //COMPLETAR CON LOS ERRORES SEMANTICOS
    public static void insertar_simbolo_declaracion(SymbolTable tabla_simbolos, Token token_simbolo, Symbol.Types tipo_simbolo,
                                                    int tamanyo_vector) throws AlreadyDefinedSymbolException {
        Symbol simbolo_a_insertar = null;
        String nombre_simbolo = token_simbolo.image;

        if (tamanyo_vector > 0) {
            simbolo_a_insertar = new SymbolArray(nombre_simbolo, 0, tamanyo_vector - 1, tipo_simbolo);

        } else if (tamanyo_vector < 0) {    //TODO CASO 0 NO CONTEMPLADO
            ErrorSemantico.deteccion("el tamaño del vector tiene que ser mayor que 0", token_simbolo);

        } else if (tipo_simbolo == Symbol.Types.CHAR) {
            simbolo_a_insertar = new SymbolChar(nombre_simbolo);

        } else if (tipo_simbolo == Symbol.Types.INT) {
            simbolo_a_insertar = new SymbolInt(nombre_simbolo);
            
        } else if (tipo_simbolo == Symbol.Types.BOOL) {
            simbolo_a_insertar = new SymbolBool(nombre_simbolo);
        }
        if(simbolo_a_insertar!=null) tabla_simbolos.insertSymbol(simbolo_a_insertar);
    }
}
