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
import lib.symbolTable.*;
import lib.symbolTable.exceptions.AlreadyDefinedSymbolException;
import traductor.*;
import lib.attributes.*;

import java.util.ArrayList;

public class SemanticFunctions {
    private ErrorSemantico errSem; //clase comun de errores semanticos

    public SemanticFunctions() {
        errSem = new ErrorSemantico();
    }

    public static void insertar_simbolo_declaracion(SymbolTable tabla_simbolos, Token token_simbolo, Symbol.Types tipo_simbolo,
                                                    Integer tamanyo_vector) throws AlreadyDefinedSymbolException {
        String nombre_simbolo = token_simbolo.image;
        Symbol simbolo_a_insertar = define_tipo_simbolo(token_simbolo, tipo_simbolo, tamanyo_vector, nombre_simbolo);
        
        if (simbolo_a_insertar != null) tabla_simbolos.insertSymbol(simbolo_a_insertar);
    }

    private static Symbol define_tipo_simbolo(Token token_simbolo, Symbol.Types tipo_simbolo, Integer tamanyo_vector, String nombre_simbolo) {
        Symbol simbolo_a_insertar = null;
        if (tamanyo_vector != null && tamanyo_vector > 0) {
            simbolo_a_insertar = new SymbolArray(nombre_simbolo, 0, tamanyo_vector - 1, tipo_simbolo);
        } else if (tamanyo_vector != null && tamanyo_vector <= 0) {
            ErrorSemantico.deteccion("el tamaño del vector tiene que ser mayor que 0", token_simbolo);
            simbolo_a_insertar =  new SymbolArray(nombre_simbolo, 0, 0, tipo_simbolo);

        } else if (tipo_simbolo == Symbol.Types.CHAR) {
            simbolo_a_insertar = new SymbolChar(nombre_simbolo);

        } else if (tipo_simbolo == Symbol.Types.INT) {
            simbolo_a_insertar = new SymbolInt(nombre_simbolo);

        } else if (tipo_simbolo == Symbol.Types.BOOL) {
            simbolo_a_insertar = new SymbolBool(nombre_simbolo);
        }
        return simbolo_a_insertar;
    }

    public static Symbol insertar_parametro(SymbolTable tabla_simbolos, Token token_simbolo, Symbol.Types tipo_simbolo,
                                            Integer tamanyo_vector, Symbol.ParameterClass clase_simbolo) throws AlreadyDefinedSymbolException {
        String nombre_simbolo = token_simbolo.image;
        Symbol simbolo_a_insertar = define_tipo_simbolo(tabla_simbolos, token_simbolo, tipo_simbolo, tamanyo_vector, clase_simbolo, nombre_simbolo);

        if(simbolo_a_insertar !=null) tabla_simbolos.insertSymbol(simbolo_a_insertar);


        return(simbolo_a_insertar);
    }

    private static Symbol define_tipo_simbolo(SymbolTable tabla_simbolos, Token token_simbolo, Symbol.Types tipo_simbolo, Integer tamanyo_vector, Symbol.ParameterClass clase_simbolo, String nombre_simbolo) throws AlreadyDefinedSymbolException {
        Symbol simbolo_a_insertar = null;
        if (tamanyo_vector != null && tamanyo_vector > 0) {
            simbolo_a_insertar = new SymbolArray(nombre_simbolo, 0, tamanyo_vector - 1, tipo_simbolo, clase_simbolo);
        } else if (tamanyo_vector != null && tamanyo_vector <= 0) {
            ErrorSemantico.deteccion("el tamaño del vector tiene que ser mayor que 0", token_simbolo);

        } else if (tipo_simbolo == Symbol.Types.CHAR) {
            simbolo_a_insertar = new SymbolChar(nombre_simbolo, clase_simbolo);

        } else if (tipo_simbolo == Symbol.Types.INT) {
            simbolo_a_insertar = new SymbolInt(nombre_simbolo, clase_simbolo);

        } else if (tipo_simbolo == Symbol.Types.BOOL) {
            simbolo_a_insertar = new SymbolBool(nombre_simbolo, clase_simbolo);
        }
        return simbolo_a_insertar;
    }

    public static void insertar_parametro_en_funcion(Symbol simbolo_fun_proc, Symbol parametro) {
        if(simbolo_fun_proc instanceof SymbolFunction){
            ((SymbolFunction) simbolo_fun_proc).setParametro(parametro);
        }else if(simbolo_fun_proc instanceof SymbolProcedure){
            ((SymbolProcedure) simbolo_fun_proc).setParametro(parametro);
        }

    }
    public static void comprobacion_numero_argumentos(SymbolFunction simbolo_funcion, SymbolProcedure simbolo_procedimiento,
                                       int indice, Token t ){
         if (simbolo_funcion != null && simbolo_funcion.parList.size() > indice) { //funcion
             ErrorSemantico.deteccion("No se han proporcionado suficientes argumentos (recibidos "+
                     (indice) + ", esperados " + simbolo_funcion.parList.size() + ")",t);
         } else if(simbolo_procedimiento != null && simbolo_procedimiento.parList.size() > indice ){//procedimiento
             ErrorSemantico.deteccion("No se han proporcionado suficientes argumentos (recibidos " +
                     (indice) + ", esperados " + simbolo_procedimiento.parList.size() + ")",t);
         }
    }
        /*
        No se puede asignar mediante get a un vector completo
     */
    public static void comprobacion_no_asignacion_vector(Attributes atr_v, Symbol sim_v, Token t){
        if(sim_v != null && ((Symbol)sim_v).type == Symbol.Types.ARRAY && atr_v == null){
             ErrorSemantico.deteccion("No se puede leer un vector",t);
        }
    }

    public static void comprobaciones_para_acciones_sin_parametros(Symbol accion, Token t){
        int dim=0;
        if(accion instanceof SymbolFunction) {
            dim = ((SymbolFunction) accion).parList.size();
        }else if(accion instanceof SymbolProcedure){
            dim = ((SymbolProcedure) accion).parList.size();
        }
        if(dim > 0){
            ErrorSemantico.deteccion("No se ha proporcionado ningún argumento (esperados "+ dim + ")",t);
        }
    }

}
