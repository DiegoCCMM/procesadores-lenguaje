//*****************************************************************
// Tratamiento de errores sintácticos
//
// Fichero:    SemanticFunctions.java
// Fecha:      03/03/2022
// Versión:    v1.0
// Asignatura: Procesadores de Lenguajes, curso 2021-2022
//*****************************************************************

package lib.tools;

import java.util.*;

import traductor.Token;
import lib.attributes.*;
import lib.symbolTable.*;
import lib.symbolTable.SymbolTable;
import lib.symbolTable.exceptions.*;
import lib.errores.*;

public class SemanticFunctions {
    private ErrorSemantico errSem; //clase común de errores semánticos

    public SemanticFunctions() {
        errSem = new ErrorSemantico();
    }

    //COMPLETAR CON LOS ERRORES SEMÁNTICOS
    public static void insertar_simbolo_declaracion(SymbolTable tabla_simbolos, Token token_simbolo, Symbol.types tipo_simbolo,
                                                    int tamanyo_vector) throws AlreadyDefinedSymbolException {
        Symbol simbolo_a_insertar;
        String nombre_simbolo = token_simbolo.image;

        if (tamanyo_vector > 0) {
            simbolo_a_insertar = SymbolArray(nombre_simbolo, 0, tamanyo_vector - 1, tipo_simbolo);

        } else if (tamanyo_vector < 0) {    //TODO CASO 0 NO CONTEMPLADO
            ErrorSemantico.deteccion("el tamaño del vector tiene que ser mayor que 0", token_simbolo);

        } else if (tipo_simbolo == Symbol.Types.CHAR) {
            simbolo_a_insertar = SymbolChar(nombre_simbolo);
            tabla_simbolos.insertSymbol(simbolo_a_insertar);

        } else if (tipo_simbolo == Symbol.Types.INT) {
            simbolo_a_insertar = SymbolInt(nombre_simbolo);
            tabla_simbolos.insertSymbol(simbolo_a_insertar);
            
        } else if (tipo_simbolo == Symbol.Types.BOOL) {
            simbolo_a_insertar = SymbolBool(nombre_simbolo);
            tabla_simbolos.insertSymbol(simbolo_a_insertar);
        
        }
    }
}
